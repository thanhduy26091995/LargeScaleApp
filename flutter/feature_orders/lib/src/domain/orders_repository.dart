import 'order.dart';

/// Repository interface for orders data.
abstract class OrdersRepository {
  Future<List<Order>> getOrders();
}
