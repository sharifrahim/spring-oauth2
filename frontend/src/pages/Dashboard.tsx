import { Link } from "react-router-dom";
import Panel from "../components/Panel";
import { useMe } from "../api/hooks";

function Dashboard() {
  const { data, isLoading, error } = useMe();
  const handleLogout = async () => {
    try {
      await fetch("/api/logout", { method: "POST", credentials: "include" });
    } finally {
      window.location.href = "/";
    }
  };

  if (isLoading) {
    return (
      <Panel title="Loading account">
        <div className="flex items-center gap-3 text-sm text-slate-300">
          <span className="h-2 w-2 animate-pulse rounded-full bg-accent shadow-[0_0_20px_rgba(59,245,155,0.7)]" />
          Fetching session…
        </div>
      </Panel>
    );
  }

  if (error) {
    const code = error.response?.data?.code;
    const message = error.response?.data?.message ?? "Something went wrong";
    const summary =
      code === "rate_limited"
        ? "Rate limit reached. Please wait before trying again."
        : code === "suspended"
          ? "This account is suspended."
          : message;

    return (
      <Panel title="Session issue">
        <p className="text-sm text-red-200">{summary}</p>
      </Panel>
    );
  }

  if (!data) {
    return null;
  }

  return (
    <div className="grid gap-6 md:grid-cols-[220px,1fr]">
      <aside className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
        <div className="mb-6">
          <p className="text-xs uppercase tracking-[0.25em] text-slate-500">Menu</p>
        </div>
        <nav className="grid gap-2">
          <Link
            to="/app"
            className="rounded-lg bg-slate-900 px-3 py-2 text-sm font-semibold text-white shadow-sm"
          >
            Dashboard
          </Link>
          <Link
            to="/app/profile"
            className="rounded-lg px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-100"
          >
            Profile
          </Link>
          <button
            onClick={handleLogout}
            className="mt-4 rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-800 transition hover:border-slate-500"
          >
            Logout
          </button>
        </nav>
      </aside>

      <div className="min-h-[320px] rounded-2xl border border-dashed border-slate-200 bg-white p-6 text-center text-slate-400">
        <p className="text-sm font-semibold text-slate-500">Dashboard</p>
        <p className="mt-2 text-sm">No widgets yet.</p>
      </div>
    </div>
  );
}

export default Dashboard;
