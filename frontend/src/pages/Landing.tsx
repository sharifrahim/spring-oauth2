import { Link } from "react-router-dom";

function Landing() {
  return (
    <div className="grid gap-12">
      <section id="home" className="grid items-center gap-10 lg:grid-cols-2">
        <div className="space-y-6">
          <p className="text-xs uppercase tracking-[0.35em] text-accent font-mono">Modern SaaS shell</p>
          <h1 className="text-4xl md:text-5xl font-semibold font-display leading-tight text-slate-900">
            Launch and operate your product from one clear console.
          </h1>
          <p className="text-lg text-slate-600">
            A ready-to-ship foundation: backend API, secure sessions, rate controls, and a clean SPA workspace so you
            can focus on your product, not plumbing.
          </p>
          <div className="flex flex-wrap gap-4">
            <button
              onClick={() => (window.location.href = "/api/login/google")}
              className="rounded-lg bg-accent px-6 py-3 text-sm font-semibold text-ink shadow-[0_16px_60px_rgba(59,245,155,0.35)] transition hover:-translate-y-0.5 hover:shadow-[0_20px_70px_rgba(59,245,155,0.45)]"
            >
              Start free
            </button>
            <Link
              to="/app"
              className="rounded-lg border border-slate-200 px-6 py-3 text-sm font-semibold text-slate-800 transition hover:border-slate-400"
            >
              View console
            </Link>
          </div>
          <div className="grid grid-cols-2 gap-4 text-sm text-slate-600">
            <Stat title="Architecture" value="BFF + SPA" />
            <Stat title="Sessions" value="Same-site cookies" />
            <Stat title="Performance" value="Fast Vite frontend" />
            <Stat title="Reliability" value="Rate controls baked in" />
          </div>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-[0_30px_90px_rgba(15,23,42,0.08)]">
          <div className="flex items-center justify-between rounded-xl border border-slate-100 bg-slate-50 p-4">
            <div>
              <p className="text-xs font-mono uppercase tracking-wide text-slate-500">Workspace</p>
              <p className="text-base font-semibold text-slate-900">One place to manage your product runtime</p>
            </div>
            <span className="h-2 w-2 rounded-full bg-accent shadow-[0_0_12px_3px_rgba(59,245,155,0.7)]" />
          </div>
          <div className="mt-6 space-y-4">
            <CardLine label="API base" value="/api/**" />
            <CardLine label="Frontend" value="React + Vite SPA" />
            <CardLine label="Controls" value="Rate limits + session security" />
            <CardLine label="Deploy" value="Run locally or package for cloud" />
          </div>
        </div>
      </section>

      <section id="plans" className="grid gap-6">
        <div className="flex flex-wrap items-center gap-3">
          <p className="text-xs uppercase tracking-[0.35em] text-accent font-mono">Plans</p>
          <div className="h-px flex-1 bg-gradient-to-r from-accent/50 via-slate-200 to-transparent" />
        </div>
        <div className="grid gap-4 md:grid-cols-3">
          <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-[0_20px_70px_rgba(15,23,42,0.08)]">
            <h3 className="text-lg font-semibold text-slate-900">Starter</h3>
            <p className="mt-2 text-sm text-slate-600">Local dev ready. Secure sessions, profile flows, rate controls, clean UI.</p>
          </article>
          <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-[0_20px_70px_rgba(15,23,42,0.08)]">
            <h3 className="text-lg font-semibold text-slate-900">Growth</h3>
            <p className="mt-2 text-sm text-slate-600">Hardened defaults, richer metrics, custom domains, tunable rate policies.</p>
          </article>
          <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-[0_20px_70px_rgba(15,23,42,0.08)]">
            <h3 className="text-lg font-semibold text-slate-900">Enterprise</h3>
            <p className="mt-2 text-sm text-slate-600">SLA, compliance hardening, deployment playbooks, and hands-on support.</p>
          </article>
        </div>
      </section>

      <section id="about" className="rounded-3xl border border-slate-200 bg-white p-6 shadow-[0_24px_80px_rgba(15,23,42,0.08)]">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <p className="text-xs uppercase tracking-[0.35em] text-accent font-mono">About</p>
            <h3 className="text-2xl font-semibold text-slate-900">A platform spine for your product</h3>
          </div>
          <Link
            to="/app"
            className="rounded-full border border-accent/40 bg-accent/10 px-4 py-2 text-xs font-semibold uppercase tracking-wide text-accent transition hover:border-accent/80 hover:bg-accent/20"
          >
            Go to console
          </Link>
        </div>
        <div className="mt-5 grid gap-3 md:grid-cols-3 text-sm text-slate-700">
          <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
            <p className="font-semibold text-slate-900">Backend</p>
            <p className="mt-2 text-slate-600">Spring Boot BFF with secure sessions, rate limiting, JSON APIs, and structured logging.</p>
          </div>
          <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
            <p className="font-semibold text-slate-900">Frontend</p>
            <p className="mt-2 text-slate-600">React + Vite SPA consuming `/api/**` with session cookies.</p>
          </div>
          <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
            <p className="font-semibold text-slate-900">Observability</p>
            <p className="mt-2 text-slate-600">Prometheus metrics and JSON logs so ops has full visibility from day one.</p>
          </div>
        </div>
      </section>

      <section id="contact" className="rounded-3xl border border-slate-200 bg-white p-6 shadow-[0_24px_80px_rgba(15,23,42,0.08)]">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <p className="text-xs uppercase tracking-[0.35em] text-accent font-mono">Contact</p>
            <h3 className="text-2xl font-semibold text-slate-900">Talk to us</h3>
            <p className="mt-2 text-slate-600">Email: contact@example.com</p>
            <p className="text-slate-600">We’ll help you ship secure login without slowing your roadmap.</p>
          </div>
          <button
            onClick={() => (window.location.href = "/api/login/google")}
            className="rounded-full bg-accent px-5 py-3 text-sm font-semibold text-ink shadow-[0_14px_50px_rgba(59,245,155,0.35)] transition hover:-translate-y-0.5 hover:shadow-[0_18px_60px_rgba(59,245,155,0.45)]"
          >
            Login
          </button>
        </div>
      </section>
    </div>
  );
}

function Stat({ title, value }: { title: string; value: string }) {
  return (
    <div className="rounded-xl border border-slate-200 bg-white p-3">
      <p className="text-xs uppercase tracking-wide text-slate-500">{title}</p>
      <p className="text-sm font-semibold text-slate-900">{value}</p>
    </div>
  );
}

function CardLine({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-center justify-between rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
      <span className="text-xs font-mono uppercase tracking-wide text-slate-500">{label}</span>
      <span className="text-sm font-semibold text-slate-900">{value}</span>
    </div>
  );
}

export default Landing;
