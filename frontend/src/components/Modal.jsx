import { useEffect } from 'react';

/**
 * Renders a modal dialog with header, body, optional footer, and error alert.
 *
 * @param {Object} props
 * @param {string} props.title - The modal title
 * @param {Function} props.onClose - Callback triggered to close the modal
 * @param {React.ReactNode} props.children - Content rendered inside the modal body
 * @param {React.ReactNode} [props.footer] - Optional footer content
 * @param {string} [props.error] - Optional error message displayed inside the modal
 */
export default function Modal({ title, onClose, children, footer, error }) {
  // ESC ile kapat
  useEffect(() => {
    const handler = (e) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [onClose]);

  return (
    <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <h2>{title}</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <div className="modal-body">
          {error && (
            <div className="alert alert-error" style={{ marginBottom: 16 }}>
              ⚠️ {error}
            </div>
          )}
          {children}
        </div>
        {footer && <div className="modal-footer">{footer}</div>}
      </div>
    </div>
  );
}
