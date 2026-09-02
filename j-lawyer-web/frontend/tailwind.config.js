/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Bold Typography Design System Tokens
        bg: '#0A0A0A',
        surface: '#0F0F0F',
        muted: '#1A1A1A',
        'muted-fg': '#737373',
        fg: '#FAFAFA',
        accent: {
          DEFAULT: '#FF3D00', // Vermillion — warm, urgent, visible
          hover: '#E03600',
          fg: '#0A0A0A',
        },
        border: {
          DEFAULT: '#262626',
          muted: '#1F1F1F',
          thick: '#FF3D00',
        },
        input: '#1A1A1A',
        card: '#0F0F0F',
      },
      fontFamily: {
        heading: ['"Inter Tight"', 'Inter', 'system-ui', '-apple-system', 'sans-serif'],
        sans: ['Inter', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
        display: ['"Playfair Display"', 'Georgia', 'serif'],
        mono: ['"JetBrains Mono"', '"Fira Code"', 'Menlo', 'Monaco', 'Courier New', 'monospace'],
      },
      letterSpacing: {
        tighter: '-0.06em',
        tight: '-0.04em',
        normal: '-0.01em',
        wide: '0.05em',
        wider: '0.1em',
        widest: '0.2em',
      },
      borderRadius: {
        none: '0px',
        DEFAULT: '0px',
        sm: '0px',
        md: '0px',
        lg: '0px',
        xl: '0px',
        '2xl': '0px',
        full: '0px',
      },
      boxShadow: {
        none: 'none',
      },
      keyframes: {
        'drawer-in': {
          '0%': { transform: 'translateX(100%)' },
          '100%': { transform: 'translateX(0)' },
        },
        'modal-pop': {
          '0%': { opacity: '0', transform: 'scale(0.98)' },
          '100%': { opacity: '1', transform: 'scale(1)' },
        },
      },
      animation: {
        'drawer-in': 'drawer-in 0.18s cubic-bezier(0.25, 0, 0, 1) forwards',
        'modal-pop': 'modal-pop 0.15s cubic-bezier(0.25, 0, 0, 1) forwards',
      }
    },
  },
  plugins: [],
}