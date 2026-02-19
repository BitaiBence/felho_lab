export interface UserResponse {
  id: number;
  username: string;
  createdAt: string;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
  user: UserResponse;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
}
