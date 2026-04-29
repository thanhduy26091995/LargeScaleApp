/// Domain model representing an order.
class Order {
  final String id;
  final String title;
  final double amount;
  final OrderStatus status;

  const Order({
    required this.id,
    required this.title,
    required this.amount,
    required this.status,
  });
}

enum OrderStatus { pending, processing, completed, cancelled }
