import { Navigate, NavLink, Outlet, Route, Routes, useLocation } from "react-router-dom";
import { useMe } from "./api/hooks";
import Dashboard from "./pages/Dashboard";
import Profile from "./pages/Profile";
import LoginError from "./pages/LoginError";
import Landing from "./pages/Landing";

function App() {
  const location = useLocation();
  const isAppRoute = location.pathname.startsWith("/app");
  const containerClasses = isAppRoute
    ? "relative flex min-h-screen w-full flex-col px-4 py-6 sm:px-8 lg:px-12"
    : "relative mx-auto max-w-6xl px-6 py-8";

  return (
    <div className="min-h-screen text-slate-900 bg-grid relative overflow-hidden">
      <div className="pointer-events-none absolute inset-0 bg-gradient-to-b from-white/60 via-white/40 to-transparent" />
      <div className="pointer-events-none absolute -left-40 top-10 h-72 w-72 rounded-full bg-accent/25 blur-3xl" />
      <div className="pointer-events-none absolute right-0 top-20 h-80 w-80 rounded-full bg-indigo-300/30 blur-3xl" />

      <div className={containerClasses}>
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

        <main className={`pb-12 ${isAppRoute ? "flex-1" : ""}`}>
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
  const location = useLocation();
  const { data, isLoading } = useMe();

  const navItems = [
    { label: "Dashboard", href: "/app", icon: LayoutIcon, exact: true },
    { label: "Profile", href: "/app/profile", icon: UserIcon },
  ];

  const activeItem = navItems.reduce<typeof navItems[number] | undefined>((best, item) => {
    const isMatch =
      location.pathname === item.href ||
      location.pathname.startsWith(`${item.href}/`) ||
      (!item.exact && location.pathname.startsWith(item.href));
    if (!isMatch) return best;
    if (!best || item.href.length > best.href.length) return item;
    return best;
  }, undefined);
  const userLabel = data?.profile?.displayName ?? data?.email ?? "Signed in";
  const userInitials = userLabel
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  const handleLogout = async () => {
    try {
      await fetch("/api/logout", { method: "POST", credentials: "include" });
    } finally {
      window.location.href = "/";
    }
  };

  return (
    <div className="mt-4 grid gap-6 lg:grid-cols-[240px,1fr]">
      <aside className="flex h-full flex-col rounded-2xl border border-slate-200/80 bg-white/80 p-4 shadow-[0_18px_60px_rgba(15,23,42,0.08)] backdrop-blur">
        <div className="flex items-center gap-3 rounded-xl border border-slate-100/70 bg-slate-50/70 px-3 py-2">
          <div className="grid h-10 w-10 place-items-center rounded-2xl bg-slate-900 text-sm font-black text-white">
            IC
          </div>
          <div>
            <p className="text-[10px] uppercase tracking-[0.3em] text-slate-500">Identity Console</p>
            <p className="text-xs font-semibold text-slate-900">Control surface</p>
          </div>
        </div>

        <nav className="mt-5 space-y-1">
          {navItems.map((item) => (
            <NavLink
              key={item.href}
              to={item.href}
              end={item.exact}
              className={({ isActive }) =>
                [
                  "group flex items-center gap-3 rounded-xl px-3 py-2 text-sm font-semibold transition",
                  isActive
                    ? "bg-slate-900 text-white shadow-sm"
                    : "text-slate-700 hover:bg-slate-100 hover:text-slate-900",
                ].join(" ")
              }
            >
              <item.icon className="h-4 w-4 text-slate-400 group-hover:text-inherit" />
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="mt-auto pt-4">
          <button
            onClick={handleLogout}
            className="w-full rounded-xl border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-800 transition hover:border-slate-400 hover:bg-slate-50"
          >
            Logout
          </button>
        </div>
      </aside>

      <div className="flex min-h-[70vh] flex-col gap-4 rounded-3xl border border-slate-200/80 bg-white/80 p-5 shadow-[0_22px_70px_rgba(15,23,42,0.08)] backdrop-blur">
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-2xl border border-slate-100/80 bg-slate-50/80 px-4 py-3">
          <div className="flex items-center gap-2 text-[11px] uppercase tracking-[0.25em] text-slate-500">
            <span className="h-2 w-2 rounded-full bg-accent shadow-[0_0_16px_rgba(59,245,155,0.65)]" />
            <span>{activeItem?.label ?? "Console"}</span>
          </div>
          <div className="flex items-center gap-3 text-sm text-slate-700">
            {isLoading ? (
              <div className="flex items-center gap-2">
                <span className="h-9 w-9 rounded-full bg-slate-200 animate-pulse" />
                <span className="h-3 w-24 rounded bg-slate-200 animate-pulse" />
              </div>
            ) : (
              <>
                <div className="grid h-9 w-9 place-items-center rounded-full bg-slate-900 text-xs font-semibold text-white">
                  {userInitials}
                </div>
                <div className="leading-tight">
                  <p className="font-semibold text-slate-900">{userLabel}</p>
                  <p className="text-[12px] text-slate-500">BFF session active</p>
                </div>
              </>
            )}
          </div>
        </div>

        <Outlet />
      </div>
    </div>
  );
}

function LayoutIcon(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" {...props}>
      <rect x="3.5" y="5" width="17" height="14" rx="2.5" />
      <path d="M9 5v14" />
      <path d="M3.5 11h5.5" />
    </svg>
  );
}

function UserIcon(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" {...props}>
      <circle cx="12" cy="8" r="3.2" />
      <path d="M6.5 19c.8-2.1 2.9-3.5 5.5-3.5s4.7 1.4 5.5 3.5" />
    </svg>
  );
}

export default App;
