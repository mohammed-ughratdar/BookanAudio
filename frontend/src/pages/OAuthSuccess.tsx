import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToken } from '../customHooks/TokenContext';

const OAuthSuccess = () => {
  const navigate = useNavigate();
  const { setToken } = useToken();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    const username = params.get("username");

    if (token) {
      setToken(token);
      localStorage.setItem("username", username || "");
      navigate("/dashboard");
    } else {
      navigate("/login");
    }
  }, []);

  return <p>Logging you in via OAuth...</p>;
};

export default OAuthSuccess;
