package com.amosannn.service;

import java.util.Map;

public interface UserService {

  Map<String, String> register(String username, String email, String password);

  Map<String, String> login(String username, String email, String password);

}
