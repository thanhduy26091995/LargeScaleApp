/// Network service contract.
abstract class NetworkService {
  Future<Map<String, dynamic>> get(String endpoint);
  Future<Map<String, dynamic>> post(String endpoint, Map<String, dynamic> body);
}

/// Stub implementation for scaffolding.
class StubNetworkService implements NetworkService {
  @override
  Future<Map<String, dynamic>> get(String endpoint) async => {};

  @override
  Future<Map<String, dynamic>> post(
          String endpoint, Map<String, dynamic> body) async =>
      {};
}
