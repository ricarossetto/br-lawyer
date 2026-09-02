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
        // Dark Mode: Bitcoin DeFi Aesthetic
        void: {
          DEFAULT: '#030304', // True Void
          surface: '#0F1115', // Dark Matter
          elevated: '#181B20',
          border: '#1E293B',
          muted: '#94A3B8',
        },
        btc: {
          orange: '#F7931A', // Bitcoin Orange (Primary Accent)
          burnt: '#EA580C',  // Burnt Orange (Secondary Accent)
          gold: '#FFD600',   // Digital Gold (Tertiary Accent)
          glow: 'rgba(247, 147, 26, 0.4)',
        },
        // Light Mode: Editorial Serif
        editorial: {
          ivory: '#FAFAF8',  // Primary Canvas
          surface: '#FFFFFF',
          muted: '#F5F3F0',
          gray: '#6B6B6B',
          border: '#E8E4DF',
          black: '#1A1A1A',
          gold: '#B8860B',   // Burnished Gold
          goldLight: '#D4A84B',
        },
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
          950: '#030304',
        },
        accent: {
          primary: '#F7931A', // Changed from indigo to Bitcoin Orange
          hover: '#EA580C',
          urgent: '#ef4444',
          warning: '#f59e0b',
          success: '#10b981',
          neutral: '#64748b',
        },
        gold: {
          DEFAULT: '#FFD600',
          burnished: '#B8860B',
          dark: '#8C7322',
          soft: '#F5E6A3',
          gleam: '#FDF3C6',
        }
      },
      fontFamily: {
        heading: ['"Space Grotesk"', '"Playfair Display"', 'Georgia', 'sans-serif'],
        sans: ['Inter', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
        serif: ['"Playfair Display"', 'Georgia', 'serif'],
        mono: ['"JetBrains Mono"', '"IBM Plex Mono"', 'Menlo', 'Monaco', 'Courier New', 'monospace'],
      },
      fontSize: {
        '2xs': '0.6875rem', // 11px
      },
      boxShadow: {
        'glow-orange': '0 0 20px -5px rgba(234, 88, 12, 0.5)',
        'glow-orange-lg': '0 0 30px -5px rgba(247, 147, 26, 0.6)',
        'glow-gold': '0 0 20px rgba(255, 214, 0, 0.3)',
        'glow-card': '0 0 50px -10px rgba(247, 147, 26, 0.1)',
        'editorial-sm': '0 1px 2px rgba(26, 26, 26, 0.04)',
        'editorial-md': '0 4px 12px rgba(26, 26, 26, 0.06)',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-6px)' },
        },
        'glow-pulse': {
          '0%, 100%': { opacity: '0.7', filter: 'drop-shadow(0 0 6px rgba(247,147,26,0.3))' },
          '50%': { opacity: '1', filter: 'drop-shadow(0 0 14px rgba(247,147,26,0.7))' },
        },
        'drawer-in': {
          '0%': { transform: 'translateX(100%)', opacity: '0' },
          '100%': { transform: 'translateX(0)', opacity: '1' },
        },
        'modal-pop': {
          '0%': { transform: 'scale(0.95)', opacity: '0' },
          '100%': { transform: 'scale(1)', opacity: '1' },
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
      },
      animation: {
        float: 'float 6s ease-in-out infinite',
        'glow-pulse': 'glow-pulse 3s ease-in-out infinite',
        'drawer-in': 'drawer-in 0.25s cubic-bezier(0.16, 1, 0.3, 1) forwards',
        'modal-pop': 'modal-pop 0.2s cubic-bezier(0.16, 1, 0.3, 1) forwards',
        shimmer: 'shimmer 3s ease-in-out infinite',
      }
    },
  },
  plugins: [],
}