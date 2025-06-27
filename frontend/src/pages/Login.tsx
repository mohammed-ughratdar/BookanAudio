import React, { useState } from 'react';
import { LoginForm, LoginResponse } from '../types/LoginTypes';
import { login } from '../api/auth';
import { useToken } from '../customHooks/TokenContext';

const Login: React.FC = () => {
    
  const [form, setForm] = useState<LoginForm>({
    email: '',
    password: '',
  });

  const { setToken } = useToken();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const {name, value} = e.target;
    setForm( prev => ({
        ...prev,
        [name]:value
    }))
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
        const loginResult: LoginResponse = await login(form)
        setToken(loginResult.token)

    } catch(error) {
        console.log("Login Error", error)
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        name="email"
        type="email"
        placeholder="Email"
        value={form.email}
        onChange={handleChange}
      />
      <input
        name="password"
        type="password"
        placeholder="Password"
        value={form.password}
        onChange={handleChange}
      />
      <button type="submit">Login</button>
    </form>
  );
};

export default Login;
