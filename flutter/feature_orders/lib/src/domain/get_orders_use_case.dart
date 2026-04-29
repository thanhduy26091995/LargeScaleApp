import '../domain/order.dart';
import '../domain/orders_repository.dart';

/// UseCase: fetch the list of orders for the current user.
class GetOrdersUseCase {
  final OrdersRepository _repository;

  GetOrdersUseCase(this._repository);

  Future<List<Order>> call() => _repository.getOrders();
}
