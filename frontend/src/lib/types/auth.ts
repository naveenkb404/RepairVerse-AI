export type UserRole = "USER" | "TECHNICIAN" | "ADMIN";

export type User = {
  id: string;
  fullName: string;
  email: string;
  role: UserRole;
  createdAt?: string;
};

export type LoginRequest = {
  email: string;
  password: string;
  rememberMe?: boolean;
};

export type RegisterRequest = {
  fullName: string;
  email: string;
  password: string;
  role?: UserRole;
  acceptTerms?: boolean;
};

export type ApiResponse<T> = {
  success: boolean;
  message?: string;
  data?: T;
};

export type AuthResponseData = {
  token: string;
  user: User;
};

export type AuthResponse = ApiResponse<AuthResponseData>;
