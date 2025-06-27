import React, { useState } from 'react';
import { RegisterForm } from '../types/RegisterTypes';
import { register } from '../api/auth';


const Register: React.FC = () => {
    
  const [form, setForm] = useState<RegisterForm>({
    username: '',
    email: '',
    password: ''
  });

  const [registerResponse, setRegisterResponse] = useState<string>("")

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
        const isSuccess = await register(form)
        
        if(isSuccess){
            console.log("Successfully Registered!")
            setRegisterResponse("Successfully Registered. Proceed to Login")
        }
    }
    catch(error){
        console.log("Not Registered", error)
        setRegisterResponse(`Something went wrong ${error}`)
    }
    };


  return (
    <>
    <form onSubmit={handleSubmit}>
      <input
        name="username"
        type="text"
        placeholder="Username"
        value={form.username}
        onChange={handleChange}
      />
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
      <button type="submit">Register</button>
    </form>

    <p>{registerResponse}</p>
    </>
  );
};

export default Register;
