import 'dart:async';
import 'package:contracts/contracts.dart';

/// Concrete implementation of [EventBus] backed by a broadcast [StreamController].
///
/// All events must extend [ModuleEvent]. This mirrors the Android [EventBus]
/// interface backed by SharedFlow<ModuleEvent>.
class AppEventBus implements EventBus {
  final StreamController<ModuleEvent> _controller =
      StreamController<ModuleEvent>.broadcast();

  /// Returns a stream filtered to events of type [T].
  @override
  Stream<T> on<T extends ModuleEvent>() {
    return _controller.stream
        .where((event) => event is T)
        .cast<T>();
  }

  /// Publishes a [ModuleEvent] to all subscribers of that event type.
  @override
  void publish(ModuleEvent event) {
    if (!_controller.isClosed) {
      _controller.add(event);
    }
  }

  /// Disposes the event bus. Call on app shutdown.
  void dispose() {
    _controller.close();
  }
}



