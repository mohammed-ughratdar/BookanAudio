
import { oauthLogin } from '../api/auth';

const OAuthLogin: React.FC = () => {
    const handleOAuth = () => {
      oauthLogin(); // this will redirect the browser
    };
  
    return (
      <button onClick={handleOAuth}>Login with Google</button>
    );
  };
  
  export default OAuthLogin;