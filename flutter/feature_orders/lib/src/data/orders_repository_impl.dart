import '../domain/order.dart';
import '../domain/orders_repository.dart';

/// Stub repository — returns hardcoded data for scaffolding.
class OrdersRepositoryImpl implements OrdersRepository {
  @override
  Future<List<Order>> getOrders() async {
    return const [
      Order(id: '1', title: 'Order #1001', amount: 45.00, status: OrderStatus.completed),
      Order(id: '2', title: 'Order #1002', amount: 120.50, status: OrderStatus.processing),
      Order(id: '3', title: 'Order #1003', amount: 89.99, status: OrderStatus.pending),
    ];
  }
}
