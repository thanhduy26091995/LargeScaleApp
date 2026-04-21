---
phase: requirements
title: Requirements & Problem Understanding — Order Cart
description: Clarify the problem space, gather requirements, and define success criteria
---

# Requirements & Problem Understanding — Order Cart

## Problem Statement
**What problem are we solving?**

- Customers have no in-app way to collect multiple items before placing an order.
- Currently there is no cart abstraction: items must be ordered one at a time, causing friction and abandoned sessions.
- Affected users: all customers interacting with the product catalogue.
- Current workaround: customers note down items manually and create separate orders.

## Goals & Objectives
**What do we want to achieve?**

### Primary goals
- Allow a CUSTOMER to add one or more items to a persistent cart.
- Allow a CUSTOMER to view, update quantity, and remove items from the cart.
- Allow a CUSTOMER to submit the cart as a single order when ready.

### Secondary goals
- Surface a cart badge/summary widget on the dashboard home screen so customers always see their cart count.
- Publish a `CartSubmittedEvent` so the orders module (and analytics) can react without direct coupling.

### Non-goals
- Payment processing (handled by `:feature-wallet`).
- Inventory reservation / stock locking (out of scope for this iteration).
- Guest checkout (CUSTOMER role is required).
- Saved / wishlist carts.

## User Stories & Use Cases

| # | Story |
|---|-------|
| US-1 | As a **customer**, I want to **add an item to my cart** so that I can buy multiple items in one go. |
| US-2 | As a **customer**, I want to **view my cart** so that I can review what I am about to purchase. |
| US-3 | As a **customer**, I want to **change the quantity** of a cart item so that I can adjust my order without removing and re-adding it. |
| US-4 | As a **customer**, I want to **remove an item from my cart** so that I can change my mind before submitting. |
| US-5 | As a **customer**, I want to **clear the entire cart** so that I can start fresh. |
| US-6 | As a **customer**, I want to **submit my cart** so that it becomes a confirmed order. |
| US-7 | As a **customer**, I want to **see the cart item count** on the home screen so that I always know what is in my cart without navigating to the cart screen. |

### Key workflows
1. **Add to cart**: customer taps "Add to Cart" on a product → item appears in cart (quantity += 1 if already present).
2. **Cart management**: customer navigates to `/cart` → sees item list, total price, and action buttons.
3. **Checkout**: customer taps "Submit Order" → confirmation dialog → `CartSubmittedEvent` published → cart cleared.

### Edge cases
- Adding the same item multiple times increments quantity rather than creating duplicates.
- Submitting an empty cart is disabled (button greyed out).
- Cart persists across app restarts (local DataStore / Room).

## Success Criteria

- [ ] A customer can add an item and see it reflected in the cart immediately.
- [ ] Cart item count badge updates on the dashboard home widget in real time.
- [ ] Submitting the cart publishes `CartSubmittedEvent` with correct payload.
- [ ] Cart is cleared after successful submission.
- [ ] No direct import between `:feature-order-cart` and any other `:feature-*` module.
- [ ] All new code has ≥ 80 % unit test coverage.

## Constraints & Assumptions

### Technical constraints
- Must follow the existing Wire Core pattern: no direct dependencies between feature modules.
- Module dependencies: `:contracts`, `:shared-ui` only (same as `:feature-orders`).
- Role gate: `Role.CUSTOMER` only.
- Navigation via `Routes.CART` constant added to `:contracts`.
- Cross-module communication via `EventBus` (`CartSubmittedEvent` added to `ModuleEvent.kt`).

### Business constraints
- Cart is per-device/per-user session (no server-side cart sync in v1).

### Assumptions
- The product catalogue already exposes an `Item` data class (or we define `CartItem` locally).
- `:feature-inventory` may publish item data via events in a future iteration; for now, items are passed directly as navigation arguments.

## Questions & Open Items

| # | Question | Owner | Status |
|---|----------|-------|--------|
| Q-1 | Should the cart persist across user logout/login? | Product | Open |
| Q-2 | Maximum number of items or quantity per cart? | Product | Open |
| Q-3 | Should quantity be limited by available stock? | Backend | Open |
| Q-4 | What payload should `CartSubmittedEvent` carry — full item list or just a cart ID? | Architecture | Open |
