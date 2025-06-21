export type LoginForm = {
    email: string,
    password : string,
}

export type LoginResponse = {
    token: string;
    username: string;
  };