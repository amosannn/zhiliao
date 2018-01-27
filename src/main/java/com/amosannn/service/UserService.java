package com.amosannn.service;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public interface UserService {

  Map<String, String> register(String username, String email, String password);

  Map<String, String> login(String username, String email, String password, HttpServletResponse response);

}
