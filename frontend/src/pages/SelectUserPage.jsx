import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginUser, createUser } from '../api/api';
import { useUser } from '../context/UserContext';

export default function SelectUserPage() {
  const [mode, setMode] = useState('login'); // 'login' | 'register'
  const navigate = useNavigate();
  const { login } = useUser();

  // Login form
  const [loginForm, setLoginForm] = useState({ email: '', password: '' });
  const [loginError, setLoginError] = useState('');
  const [loginLoading, setLoginLoading] = useState(false);

  // Register form
  const [registerForm, setRegisterForm] = useState({ name: '', email: '', password: '', confirmPassword: '' });
  const [registerError, setRegisterError] = useState('');
  const [registerLoading, setRegisterLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoginError('');
    if (!loginForm.email || !loginForm.password) {
      setLoginError('E-posta ve şifre alanları boş bırakılamaz.');
      return;
    }
    try {
      setLoginLoading(true);
      const res = await loginUser({ email: loginForm.email, password: loginForm.password });
      login(res.data);
      navigate('/dashboard');
    } catch (err) {
      const msg = err.response?.data?.message;
      setLoginError(msg || 'E-posta veya şifre hatalı.');
    } finally {
      setLoginLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setRegisterError('');
    if (!registerForm.name || !registerForm.email || !registerForm.password) {
      setRegisterError('Tüm alanları doldurunuz.');
      return;
    }
    if (registerForm.password !== registerForm.confirmPassword) {
      setRegisterError('Şifreler eşleşmiyor.');
      return;
    }
    if (registerForm.password.length < 6) {
      setRegisterError('Şifre en az 6 karakter olmalıdır.');
      return;
    }
    try {
      setRegisterLoading(true);
      const res = await createUser({
        name: registerForm.name,
        email: registerForm.email,
        password: registerForm.password,
        confirmPassword: registerForm.confirmPassword,
      });
      login(res.data);
      navigate('/dashboard');
    } catch (err) {
      setRegisterError(err.response?.data?.message || 'Kayıt oluşturulamadı.');
    } finally {
      setRegisterLoading(false);
    }
  };

  return (
    <div className="auth-page">
      {/* Arka plan efektleri */}
      <div className="auth-bg-blob auth-bg-blob--1" />
      <div className="auth-bg-blob auth-bg-blob--2" />

      <div className="auth-container">
        {/* Logo */}
        <div className="auth-logo">
          <span className="auth-logo-icon">🏡</span>
          <span className="auth-logo-text">Nestify</span>
        </div>

        {/* Kart */}
        <div className="auth-card">
          {mode === 'login' ? (
            <>
              <div className="auth-card-header">
                <h1>Tekrar hoş geldiniz</h1>
                <p>Devam etmek için hesabınıza giriş yapın.</p>
              </div>

              {loginError && (
                <div className="alert alert-error">⚠️ {loginError}</div>
              )}

              <form onSubmit={handleLogin} className="auth-form">
                <div className="form-group">
                  <label className="form-label">E-Posta</label>
                  <input
                    className="form-input"
                    type="email"
                    placeholder="ornek@mail.com"
                    value={loginForm.email}
                    onChange={(e) => setLoginForm((f) => ({ ...f, email: e.target.value }))}
                    autoComplete="email"
                    autoFocus
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Şifre</label>
                  <input
                    className="form-input"
                    type="password"
                    placeholder="••••••••"
                    value={loginForm.password}
                    onChange={(e) => setLoginForm((f) => ({ ...f, password: e.target.value }))}
                    autoComplete="current-password"
                  />
                </div>
                <button
                  type="submit"
                  className="btn btn-primary w-full"
                  style={{ marginTop: 8, padding: '12px', fontSize: '0.95rem' }}
                  disabled={loginLoading}
                >
                  {loginLoading ? <><span className="btn-spinner" /> Giriş yapılıyor...</> : '→ Giriş Yap'}
                </button>
              </form>

              <div className="auth-switch">
                <span>Hesabınız yok mu?</span>
                <button
                  className="auth-switch-btn"
                  onClick={() => { setMode('register'); setLoginError(''); }}
                >
                  Kayıt Ol
                </button>
              </div>
            </>
          ) : (
            <>
              <div className="auth-card-header">
                <h1>Hesap oluşturun</h1>
                <p>Nestify'a katılmak için bilgilerinizi girin.</p>
              </div>

              {registerError && (
                <div className="alert alert-error">⚠️ {registerError}</div>
              )}

              <form onSubmit={handleRegister} className="auth-form">
                <div className="form-group">
                  <label className="form-label">Ad Soyad</label>
                  <input
                    className="form-input"
                    type="text"
                    placeholder="Ad Soyad"
                    value={registerForm.name}
                    onChange={(e) => setRegisterForm((f) => ({ ...f, name: e.target.value }))}
                    autoComplete="name"
                    autoFocus
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">E-Posta</label>
                  <input
                    className="form-input"
                    type="email"
                    placeholder="ornek@mail.com"
                    value={registerForm.email}
                    onChange={(e) => setRegisterForm((f) => ({ ...f, email: e.target.value }))}
                    autoComplete="email"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Şifre</label>
                  <input
                    className="form-input"
                    type="password"
                    placeholder="Min. 6 karakter"
                    value={registerForm.password}
                    onChange={(e) => setRegisterForm((f) => ({ ...f, password: e.target.value }))}
                    autoComplete="new-password"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Şifre Tekrar</label>
                  <input
                    className="form-input"
                    type="password"
                    placeholder="Şifreyi tekrar girin"
                    value={registerForm.confirmPassword}
                    onChange={(e) => setRegisterForm((f) => ({ ...f, confirmPassword: e.target.value }))}
                    autoComplete="new-password"
                  />
                </div>
                <button
                  type="submit"
                  className="btn btn-primary w-full"
                  style={{ marginTop: 8, padding: '12px', fontSize: '0.95rem' }}
                  disabled={registerLoading}
                >
                  {registerLoading ? <><span className="btn-spinner" /> Kaydediliyor...</> : '✓ Hesap Oluştur'}
                </button>
              </form>

              <div className="auth-switch">
                <span>Zaten hesabınız var mı?</span>
                <button
                  className="auth-switch-btn"
                  onClick={() => { setMode('login'); setRegisterError(''); }}
                >
                  Giriş Yap
                </button>
              </div>
            </>
          )}
        </div>

        <p className="auth-footer">Nestify &copy; 2025 — Ev Yönetim Sistemi</p>
      </div>
    </div>
  );
}
