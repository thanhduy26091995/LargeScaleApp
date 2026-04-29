import 'package:flutter/material.dart';

class AvatarWidget extends StatelessWidget {
  final String initials;
  final String? imageUrl;
  final double size;

  const AvatarWidget({
    super.key,
    required this.initials,
    this.imageUrl,
    this.size = 40,
  });

  @override
  Widget build(BuildContext context) {
    return CircleAvatar(
      radius: size / 2,
      backgroundImage: imageUrl != null ? NetworkImage(imageUrl!) : null,
      child: imageUrl == null ? Text(initials) : null,
    );
  }
}
