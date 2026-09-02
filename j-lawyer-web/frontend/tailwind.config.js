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
        mineral: {
          50: '#f8fafc',
          100: '#f1f5f9',
          200: '#e2e8f0',
          300: '#cbd5e1',
          400: '#94a3b8',
          500: '#64748b',
          600: '#475569',
          700: '#334155',
          800: '#1e293b',
          900: '#0f172a',
          950: '#020617',
        },
        accent: {
          primary: '#4f46e5', // indigo
          hover: '#4338ca',
          urgent: '#ef4444', // crimson
          warning: '#f59e0b', // amber
          success: '#10b981', // emerald
          neutral: '#64748b', // slate
        },
        gold: {
          DEFAULT: '#D4AF37',
          dark: '#8C7322',
          soft: '#F5E6A3',
          gleam: '#FDF3C6',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
        serif: ['"Playfair Display"', 'Georgia', 'serif'],
        mono: ['JetBrains Mono', 'Fira Code', 'Menlo', 'Monaco', 'Courier New', 'monospace'],
      },
      fontSize: {
        '2xs': '0.6875rem', // 11px
      }
    },
  },
  plugins: [],
}