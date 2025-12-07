import { Navigate, Outlet, Route, Routes, useLocation } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import Profile from "./pages/Profile";
import LoginError from "./pages/LoginError";
import Landing from "./pages/Landing";

function App() {
  const location = useLocation();
  const isAppRoute = location.pathname.startsWith("/app");

  return (
    <div className="min-h-screen text-slate-900 bg-grid relative overflow-hidden">
      <div className="pointer-events-none absolute inset-0 bg-gradient-to-b from-white/60 via-white/40 to-transparent" />
      <div className="pointer-events-none absolute -left-40 top-10 h-72 w-72 rounded-full bg-accent/25 blur-3xl" />
      <div className="pointer-events-none absolute right-0 top-20 h-80 w-80 rounded-full bg-indigo-300/30 blur-3xl" />

      <div className={`relative ${isAppRoute ? "max-w-6xl" : "max-w-6xl"} mx-auto px-6 py-8`}>
        {!isAppRoute && (
          <header className="flex flex-wrap items-center justify-between gap-4 pb-6">
            <div className="flex items-center gap-3">
              <div className="h-10 w-10 rounded-2xl bg-white shadow-md grid place-items-center text-accent font-black text-lg">
                IC
              </div>
              <div>
                <p className="text-xs uppercase tracking-[0.3em] text-slate-500">Identity Console</p>
                <p className="text-sm text-slate-500">OAuth2 BFF + SPA</p>
              </div>
            </div>
            <nav className="flex flex-wrap items-center gap-3">
              <a
                href="#home"
                className="rounded-full px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-white"
              >
                Home
              </a>
              <a
                href="#plans"
                className="rounded-full px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-white"
              >
                Plans
              </a>
              <a
                href="#about"
                className="rounded-full px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-white"
              >
                About
              </a>
              <a
                href="#contact"
                className="rounded-full px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-white"
              >
                Contact
              </a>
              <button
                onClick={() => (window.location.href = "/api/login/google")}
                className="rounded-full bg-accent px-4 py-2 text-sm font-semibold text-ink shadow-[0_12px_40px_rgba(59,245,155,0.35)] transition hover:-translate-y-0.5 hover:shadow-[0_16px_50px_rgba(59,245,155,0.45)]"
              >
                Login
              </button>
            </nav>
          </header>
        )}

        <main className="pb-12">
          <Routes>
            <Route path="/" element={<Landing />} />
            <Route element={<AppShell />}>
              <Route path="/app" element={<Dashboard />} />
              <Route path="/app/profile" element={<Profile />} />
            </Route>
            <Route path="/login-error" element={<LoginError />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </div>
    </div>
  );
}

function AppShell() {
  return (
    <div className="mt-6 grid gap-6">
      <Outlet />
    </div>
  );
}

export default App;
