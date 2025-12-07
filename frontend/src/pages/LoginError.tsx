import Panel from "../components/Panel";

function LoginError() {
  return (
    <Panel title="Login failed" description="OAuth provider rejected or cancelled the sign-in.">
      <p className="text-sm text-red-200">
        Please retry with Google. If the issue persists, verify your account or try again later.
      </p>
      <div className="mt-4 flex gap-3">
        <button
          className="rounded-md bg-accent px-4 py-2 text-sm font-semibold text-ink shadow-[0_8px_30px_rgba(59,245,155,0.35)]"
          onClick={() => (window.location.href = "/api/login/google")}
        >
          Retry login
        </button>
        <a
          href="/api/logout"
          className="rounded-md border border-white/15 px-4 py-2 text-sm font-semibold text-slate-200 transition hover:border-white/35"
        >
          Clear session
        </a>
      </div>
    </Panel>
  );
}

export default LoginError;
