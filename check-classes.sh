#!/bin/bash
cd ~/.m2/repository/org/springframework/security
for j in spring-security-oauth2-resource-server/7.1.1/*.jar spring-security-oauth2-jose/7.1.1/*.jar spring-security-config/7.1.1/*.jar spring-security-web/7.1.1/*.jar; do
  echo "==$j=="
  unzip -l "$j" | grep -E 'JwtDecoder.class$|NimbusJwtDecoder.class$|JwtAuthenticationConverter.class$|JwtGrantedAuthoritiesConverter.class$|SecurityFilterChain.class$'
done
