import Combine
import Foundation
import Contracts

/// Type-safe pub/sub event bus backed by Combine.
///
/// All events must inherit from `ModuleEvent`. This mirrors Android `EventBus`
/// (backed by SharedFlow<ModuleEvent>) and Flutter `AppEventBus`.
///
/// Example:
/// ```swift
/// // Publish
/// eventBus.publish(OrderCreatedEvent(orderId: "123", moduleId: "orders"))
///
/// // Subscribe (store AnyCancellable, cancel in AppModule.onDestroy)
/// eventBus.on(OrderCreatedEvent.self)
///     .sink { event in refresh() }
///     .store(in: &cancellables)
/// ```
public final class AppEventBus: EventBusProtocol {
    private let subject = PassthroughSubject<ModuleEvent, Never>()

    public init() {}

    /// Publishes a `ModuleEvent` to all subscribers of that event type.
    public func publish<T: ModuleEvent>(_ event: T) {
        subject.send(event)
    }

    /// Returns a typed publisher that emits events of type `T`.
    public func on<T: ModuleEvent>(_ type: T.Type) -> AnyPublisher<T, Never> {
        subject
            .compactMap { $0 as? T }
            .eraseToAnyPublisher()
    }
}

