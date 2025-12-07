import type { FormEvent } from "react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useMe, useUpdateProfile } from "../api/hooks";
import Panel from "../components/Panel";

function Profile() {
  const { data, isLoading, error } = useMe();
  const updateProfile = useUpdateProfile();
  const [displayName, setDisplayName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [company, setCompany] = useState("");
  const [mode, setMode] = useState<"view" | "edit">("view");

  useEffect(() => {
    if (data?.profile) {
      setDisplayName(data.profile.displayName ?? "");
      setPhoneNumber(data.profile.phoneNumber ?? "");
      setCompany(data.profile.company ?? "");
    }
  }, [data]);

  const resetForm = () => {
    if (data?.profile) {
      setDisplayName(data.profile.displayName ?? "");
      setPhoneNumber(data.profile.phoneNumber ?? "");
      setCompany(data.profile.company ?? "");
    }
  };

  const handleLogout = async () => {
    try {
      await fetch("/api/logout", { method: "POST", credentials: "include" });
    } finally {
      window.location.href = "/";
    }
  };

  if (isLoading) {
    return (
      <Panel title="Loading profile">
        <p className="text-sm text-slate-300">Pulling your profile from the BFF…</p>
      </Panel>
    );
  }

  if (error) {
    const message = error.response?.data?.message ?? "Unable to load profile.";
    return (
      <Panel title="Profile unavailable">
        <p className="text-sm text-red-600">{message}</p>
      </Panel>
    );
  }

  if (!data) {
    return null;
  }

  const onSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    updateProfile.mutate(
      { displayName, phoneNumber, company },
      {
        onSuccess: () => {
          setMode("view");
        },
      },
    );
  };

  const profileFields = [
    { label: "Display name", value: displayName || "Not set" },
    { label: "Phone number", value: phoneNumber || "Not set" },
    { label: "Company", value: company || "Not set" },
  ];

  return (
    <Panel
      title="Profile details"
      description="Stored server-side; cookies stay in the browser and never expose tokens."
      actions={
        mode === "view" ? (
          <div className="flex gap-2">
            <button
              onClick={() => setMode("edit")}
              className="rounded-lg border border-slate-300 px-4 py-2 text-xs font-semibold text-slate-800 transition hover:border-slate-500"
            >
              Edit
            </button>
            <button
              onClick={handleLogout}
              className="rounded-lg border border-slate-300 px-4 py-2 text-xs font-semibold text-slate-800 transition hover:border-slate-500"
            >
              Logout
            </button>
          </div>
        ) : updateProfile.isSuccess ? (
          <span className="rounded-full border border-accent/40 bg-accent/10 px-3 py-1 text-xs font-semibold text-accent">
            Saved
          </span>
        ) : null
      }
    >
      {mode === "view" ? (
        <div className="grid gap-4">
          {profileFields.map((field) => (
            <div
              key={field.label}
              className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 flex items-center justify-between"
            >
              <span className="text-xs font-mono uppercase tracking-wide text-slate-500">{field.label}</span>
              <span className="text-sm font-semibold text-slate-900">{field.value}</span>
            </div>
          ))}
          <div className="flex flex-wrap gap-3">
            <button
              onClick={() => setMode("edit")}
              className="rounded-lg bg-slate-900 px-4 py-3 text-sm font-semibold text-white shadow-sm transition hover:-translate-y-0.5 hover:shadow"
            >
              Edit profile
            </button>
            <Link
              to="/app"
              className="rounded-lg border border-slate-300 px-4 py-3 text-sm font-semibold text-slate-800 transition hover:border-slate-500"
            >
              Back to console
            </Link>
          </div>
        </div>
      ) : (
        <form className="grid gap-4" onSubmit={onSubmit}>
          <Field label="Display name" required>
            <input
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              required
              className="w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-900 outline-none transition focus:border-accent focus:shadow-[0_0_0_3px_rgba(59,245,155,0.2)]"
            />
          </Field>

          <Field label="Phone number">
            <input
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              className="w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-900 outline-none transition focus:border-accent focus:shadow-[0_0_0_3px_rgba(59,245,155,0.2)]"
            />
          </Field>

          <Field label="Company">
            <input
              value={company}
              onChange={(e) => setCompany(e.target.value)}
              className="w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-900 outline-none transition focus:border-accent focus:shadow-[0_0_0_3px_rgba(59,245,155,0.2)]"
            />
          </Field>

          {updateProfile.isError ? (
            <p className="text-sm text-red-600">
              {updateProfile.error?.response?.data?.message ?? "Unable to update profile."}
            </p>
          ) : null}

          <div className="flex flex-wrap gap-3">
            <button
              type="submit"
              disabled={updateProfile.isPending}
              className="rounded-lg bg-accent px-5 py-3 text-sm font-semibold text-ink shadow-[0_10px_40px_rgba(59,245,155,0.35)] transition hover:-translate-y-0.5 hover:shadow-[0_14px_50px_rgba(59,245,155,0.45)] disabled:cursor-not-allowed disabled:opacity-60"
            >
              {updateProfile.isPending ? "Saving…" : "Save profile"}
            </button>
            <button
              type="button"
              onClick={() => {
                resetForm();
                setMode("view");
              }}
              className="rounded-lg border border-slate-300 px-4 py-3 text-sm font-semibold text-slate-800 transition hover:border-slate-500"
            >
              Cancel
            </button>
            <Link
              to="/app"
              className="rounded-lg border border-slate-300 px-4 py-3 text-sm font-semibold text-slate-800 transition hover:border-slate-500"
            >
              Back to console
            </Link>
            <button
              type="button"
              onClick={handleLogout}
              className="rounded-lg border border-slate-300 px-4 py-3 text-sm font-semibold text-slate-800 transition hover:border-slate-500"
            >
              Logout
            </button>
          </div>
        </form>
      )}
    </Panel>
  );
}

function Field({ label, required, children }: { label: string; required?: boolean; children: React.ReactNode }) {
  return (
    <label className="block space-y-2">
      <span className="text-xs font-semibold uppercase tracking-wide text-slate-500">
        {label} {required ? <span className="text-accent">*</span> : null}
      </span>
      {children}
    </label>
  );
}

export default Profile;
