# ADR 001 — Wire Core Pattern for Module Orchestration

**Status**: Accepted  
**Date**: 2026-04-13  
**Deciders**: Tech Lead, Senior Developers

---

## Context

We are building a large-scale Android app that must support:
- Multiple feature teams working independently
- Runtime tenant configuration (white-labeling)
- Role-based feature gating
- Dynamic UI composition (features contributing widgets to shared screens)
- Cross-module communication without tight coupling

The key architectural question: **how do feature modules discover and communicate with each other** without creating a dependency spaghetti?

---

## Decision

We introduce a **Wire Core** layer (`:wire` module) that acts as the application's central service bus. All modules interact only through:

1. **`:contracts`** — shared interfaces and data classes (no implementation)
2. **`ModuleContext`** — a facade injected into every module at init time

Feature modules **never depend on each other**. They only depend on `:contracts`.

### The Wire Core components

| Component | Responsibility |
|---|---|
| `ModuleRegistry` | Registers modules; resolves them by role + tenant |
| `FlowEventBus` | Type-safe coroutine-based publish/subscribe |
| `SlotRegistry` | Maps `@Composable` widgets to named slot hosts |
| `AppNavigatorImpl` | Wraps `NavHostController`; provides programmatic navigation |
| `RoleManager` | Tracks current role via events (no polling) |
| `TenantResolverImpl` | Resolves `TenantConfig` for the current session |

---

## Alternatives Considered

### Alt A: Direct module-to-module imports

Each module imports what it needs from other modules directly.

**Rejected because**:
- Creates circular dependency risk
- Teams cannot work independently — a change in module A breaks module B's build
- Cannot add/remove modules at runtime per tenant
- Violates the "feature teams own their module" model

### Alt B: Central ServiceLocator (static singleton map)

A `ServiceLocator.get<T>(key)` pattern where modules register and look up services.

**Rejected because**:
- Not type-safe — runtime crashes instead of compile errors
- Hidden dependencies (no DI graph visibility)
- Hard to test — global state bleeds between tests
- Incompatible with Hilt's compile-time dependency validation

### Alt C: Shared ViewModel in `:app`

A global `AppViewModel` that all feature modules `hiltViewModel()` into.

**Rejected because**:
- `:app` module would grow unboundedly — every new cross-module concern adds code there
- Feature teams need `:app` changes for every new feature = bottleneck
- Fragments/screens in different modules can't independently own their ViewModels

### Alt D: Wire Core (chosen)

Thin orchestration layer with explicit contracts and event-driven decoupling.

**Accepted because**:
- Dependency direction is always inward (toward `:contracts`)
- Modules are fully independent — can be compiled and tested in isolation
- `ModuleRegistry.resolve(role, tenant)` makes the runtime module set explicit and testable
- `FlowEventBus` replaces direct calls: sender and receiver are decoupled in time and space
- `SlotRegistry` allows any module to contribute UI to any screen without the screen knowing

---

## Consequences

### Positive

- **Parallel development**: teams own their module directory, no merge conflicts in shared code
- **Testability**: every Wire Core component is independently unit-testable; modules can be tested with fake `ModuleContext`
- **Tenant flexibility**: `ModuleRegistry.resolve()` filters at runtime — no recompile needed for different tenants
- **Role safety**: role checks are enforced at the registry and slot levels, not scattered across UI code

### Negative

- **Learning curve**: new developers must understand `ModuleContext`, `EventBus`, and `SlotRegistry` before contributing
- **Event debugging**: tracing a flow that spans `publish()` → `subscribe()` requires familiarity with the bus — add structured logging to `FlowEventBus` for production
- **Indirect communication**: for simple ViewModel-to-ViewModel calls, the event bus adds overhead vs. a direct function call

### Neutral

- `:wire` is a singleton module — there will only ever be one. If the app grows to require multi-process architecture, the event bus would need a transport layer (e.g., Binder, AIDL).

---

## Implementation Notes

### Dependency rule

```
:contracts  ←  everything
:wire       ←  :app only (for initialization)
:core       must NOT depend on :wire  (would create a cycle via :app)
```

`AuthServiceImpl` (in `:core`) communicates role changes to `RoleManager` (in `:wire`) **exclusively via events** — no direct dependency.

### Module initialization order

```
1. Hilt graph is built (all @Singleton instances created)
2. LargeScaleApp.onCreate()
3. registerModules() — all AppModule instances added to ModuleRegistry
4. moduleRegistry.initializeAll(moduleContext) — each module.initialize() called in priority order
5. MainActivity.onCreate()
6. NavHost built — all navGraph extensions added
```

### Role ordinal convention

```kotlin
enum class Role { ADMIN, STAFF, CUSTOMER, GUEST }
// ordinal: 0,    1,     2,        3
// userRole.ordinal <= resource.requiredRole.ordinal → access granted
```

ADMIN (0) can access everything. GUEST (3) can only access resources that explicitly require GUEST.

---

## Review

This ADR will be revisited if:
- A new cross-cutting concern cannot be expressed as an event or slot
- Startup performance is measurably degraded by `initializeAll()`
- The team size grows beyond ~10 developers (may require module grouping)
