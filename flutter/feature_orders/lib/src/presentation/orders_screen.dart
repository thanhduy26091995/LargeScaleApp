import 'package:flutter/material.dart';
import '../domain/order.dart';
import '../domain/get_orders_use_case.dart';

class OrdersScreen extends StatefulWidget {
  final GetOrdersUseCase useCase;

  const OrdersScreen({super.key, required this.useCase});

  @override
  State<OrdersScreen> createState() => _OrdersScreenState();
}

class _OrdersScreenState extends State<OrdersScreen> {
  late Future<List<Order>> _ordersFuture;

  @override
  void initState() {
    super.initState();
    _ordersFuture = widget.useCase();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Orders')),
      body: FutureBuilder<List<Order>>(
        future: _ordersFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          }
          final orders = snapshot.data ?? [];
          if (orders.isEmpty) {
            return const Center(child: Text('No orders found.'));
          }
          return ListView.builder(
            itemCount: orders.length,
            itemBuilder: (context, index) {
              final order = orders[index];
              return ListTile(
                title: Text(order.title),
                subtitle: Text('\$${order.amount.toStringAsFixed(2)}'),
                trailing: Chip(label: Text(order.status.name)),
              );
            },
          );
        },
      ),
    );
  }
}
