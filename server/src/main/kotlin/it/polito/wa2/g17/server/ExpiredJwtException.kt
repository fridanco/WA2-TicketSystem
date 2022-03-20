package it.polito.wa2.g17.server

class ExpiredJwtException : RuntimeException("JWT is expired")