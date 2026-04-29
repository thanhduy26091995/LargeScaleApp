/// Core — always-loaded shared services.
library core;

// Re-export AuthService interface (lives in contracts, impl lives here)
export 'package:contracts/contracts.dart' show AuthService, User;

export 'src/auth/auth_service.dart';
export 'src/network/network_service.dart';
export 'src/storage/storage_service.dart';
