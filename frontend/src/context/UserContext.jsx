import { createContext, useContext, useState, useEffect } from 'react';
import { setActingUserId, setAuthToken } from '../api/api';

const UserContext = createContext(null);

export function UserProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(() => {
    try {
      const stored = localStorage.getItem('nestify_user');
      if (stored) {
        const user = JSON.parse(stored);
        if (user?.token) {
          setAuthToken(user.token);
        }
        if (user?.id) {
          setActingUserId(user.id);
        }
        return user;
      }
      return null;
    } catch {
      return null;
    }
  });

  useEffect(() => {
    if (currentUser) {
      setActingUserId(currentUser.id);
      setAuthToken(currentUser.token);
      localStorage.setItem('nestify_user', JSON.stringify(currentUser));
    } else {
      setActingUserId(null);
      setAuthToken(null);
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
    if (user.token) {
      setAuthToken(user.token);
    }
    if (user.id) {
      setActingUserId(user.id);
    }
  };

  const logout = () => {
    setCurrentUser(null);
    setAuthToken(null);
    setActingUserId(null);
  };

  return (
    <UserContext.Provider value={{ currentUser, login, logout }}>
      {children}
    </UserContext.Provider>
  );
}

export const useUser = () => useContext(UserContext);
