import Panel from "../components/Panel";
import { useMe } from "../api/hooks";

function Dashboard() {
  const { data, isLoading, error, refetch } = useMe();
  const metrics = [
    { label: "Profile status", value: data?.profileComplete ? "Complete" : "Needs attention" },
    { label: "Auth channel", value: "Same-site cookies" },
    { label: "API scope", value: "/api/**" },
  ];

  if (isLoading) {
    return (
      <div className="grid gap-4 md:grid-cols-2">
        <SkeletonPanel />
        <SkeletonPanel />
      </div>
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
      <Panel
        title="Session issue"
        description="We couldn't load your console data."
        actions={
          <button
            onClick={() => refetch()}
            className="rounded-lg border border-slate-200 px-4 py-2 text-xs font-semibold text-slate-800 transition hover:border-slate-400 hover:bg-slate-50"
          >
            Retry
          </button>
        }
      >
        <p className="text-sm text-red-600">{summary}</p>
      </Panel>
    );
  }

  if (!data) {
    return null;
  }

  return (
    <div className="grid gap-5">
      <Panel
        title="Dashboard snapshot"
        description="Quick context on your session and environment."
        actions={
          <span className="rounded-full bg-accent/10 px-3 py-1 text-xs font-semibold text-accent">Live</span>
        }
      >
        <div className="grid gap-4 md:grid-cols-3">
          {metrics.map((metric) => (
            <div
              key={metric.label}
              className="rounded-xl border border-slate-200 bg-slate-50/80 px-4 py-3 text-sm text-slate-700"
            >
              <p className="text-[11px] uppercase tracking-[0.18em] text-slate-500">{metric.label}</p>
              <p className="mt-1 text-base font-semibold text-slate-900">{metric.value}</p>
            </div>
          ))}
        </div>
      </Panel>

      <Panel
        title="Next steps"
        description="Stay secure and ship quickly."
      >
        <div className="grid gap-3 md:grid-cols-2">
          <div className="rounded-xl border border-slate-100 bg-white px-4 py-3">
            <p className="text-sm font-semibold text-slate-900">Add your profile details</p>
            <p className="mt-1 text-sm text-slate-600">Keep account signals fresh to avoid session friction.</p>
          </div>
          <div className="rounded-xl border border-slate-100 bg-white px-4 py-3">
            <p className="text-sm font-semibold text-slate-900">Explore the API surface</p>
            <p className="mt-1 text-sm text-slate-600">Use the BFF to call `/api/**` with your session cookies.</p>
          </div>
        </div>
      </Panel>
    </div>
  );
}

function SkeletonPanel() {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-[0_18px_60px_rgba(15,23,42,0.08)]">
      <div className="flex items-center justify-between">
        <div className="h-4 w-32 rounded bg-slate-200 animate-pulse" />
        <div className="h-6 w-14 rounded-full bg-slate-200 animate-pulse" />
      </div>
      <div className="mt-4 grid gap-3">
        <div className="h-3 w-full rounded bg-slate-200 animate-pulse" />
        <div className="h-3 w-3/4 rounded bg-slate-200 animate-pulse" />
        <div className="h-3 w-2/3 rounded bg-slate-200 animate-pulse" />
      </div>
    </div>
  );
}

export default Dashboard;
