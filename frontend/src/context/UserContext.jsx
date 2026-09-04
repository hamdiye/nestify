import { createContext, useContext, useState, useEffect } from 'react';
import { setActingUserId } from '../api/api';

const UserContext = createContext(null);

export function UserProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('nestify_user')); }
    catch { return null; }
  });

  useEffect(() => {
    if (currentUser) {
      setActingUserId(currentUser.id);
      localStorage.setItem('nestify_user', JSON.stringify(currentUser));
    } else {
      setActingUserId(null);
      localStorage.removeItem('nestify_user');
    }
  }, [currentUser]);

  /**
   * AuthResponseDto'yu alıp currentUser state'ine normalleştirir.
   * @param {Object} authResponse - { userId, fullName, email, accessToken, tokenType }
   */
  const login = (authResponse) => {
    const user = {
      id:    authResponse.userId  ?? authResponse.id,
      name:  authResponse.fullName ?? authResponse.name,
      email: authResponse.email,
      token: authResponse.accessToken ?? null,
    };
    setCurrentUser(user);
  };

  const logout = () => setCurrentUser(null);

  return (
    <UserContext.Provider value={{ currentUser, login, logout }}>
      {children}
    </UserContext.Provider>
  );
}

export const useUser = () => useContext(UserContext);
