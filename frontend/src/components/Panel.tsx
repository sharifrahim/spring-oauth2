import type { PropsWithChildren } from "react";

type PanelProps = PropsWithChildren<{
  title?: string;
  description?: string;
  actions?: React.ReactNode;
}>;

function Panel({ title, description, actions, children }: PanelProps) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-[0_18px_60px_rgba(15,23,42,0.08)]">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          {title ? (
            <h2 className="text-xl font-semibold font-display text-slate-900">{title}</h2>
          ) : null}
          {description ? (
            <p className="mt-1 text-sm text-slate-600">{description}</p>
          ) : null}
        </div>
        {actions}
      </div>
      <div className="mt-4">{children}</div>
    </div>
  );
}

export default Panel;
