import type { FormEvent } from "react";
import { useEffect, useState } from "react";
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

  if (isLoading) {
    return <ProfileSkeleton />;
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
    { label: "Email", value: data.email },
    { label: "Status", value: data.status === "ACTIVE" ? "Active" : "Suspended" },
  ];

  return (
    <Panel
      title="Profile details"
      description="Stored server-side; cookies stay in the browser and never expose tokens."
      actions={
        mode === "view"
          ? null
          : updateProfile.isSuccess
            ? (
              <span className="rounded-full border border-accent/40 bg-accent/10 px-3 py-1 text-xs font-semibold text-accent">
                Saved
              </span>
            )
            : null
    }
  >
      {mode === "view" ? (
        <div className="grid gap-3">
          <div className="rounded-2xl border border-slate-200 bg-slate-50/80 px-4 py-3">
            <p className="text-[11px] uppercase tracking-[0.2em] text-slate-500">Account</p>
            <p className="mt-1 text-base font-semibold text-slate-900">{data.email}</p>
            <div className="mt-2 flex flex-wrap gap-2 text-xs">
              <span className="rounded-full bg-accent/10 px-3 py-1 font-semibold text-accent">Session active</span>
              <span className="rounded-full bg-slate-900 px-3 py-1 font-semibold text-white">
                {data.status === "ACTIVE" ? "Active" : "Suspended"}
              </span>
            </div>
          </div>

          <div className="rounded-2xl border border-slate-200 bg-white px-4 py-3">
            <p className="text-[11px] uppercase tracking-[0.2em] text-slate-500">Profile fields</p>
            <div className="mt-3 grid gap-3 sm:grid-cols-2">
              {profileFields.map((field) => (
                <div
                  key={field.label}
                  className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-3"
                >
                  <p className="text-[10px] uppercase tracking-[0.18em] text-slate-500">{field.label}</p>
                  <p className="mt-1 text-sm font-semibold text-slate-900">{field.value}</p>
                </div>
              ))}
            </div>
          </div>

          <button
            onClick={() => setMode("edit")}
            className="w-fit rounded-lg bg-slate-900 px-4 py-3 text-sm font-semibold text-white shadow-sm transition hover:-translate-y-0.5 hover:shadow"
          >
            Edit profile
          </button>
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

function ProfileSkeleton() {
  return (
    <Panel title="Loading profile">
      <div className="grid gap-3 md:grid-cols-2">
        {[...Array(4)].map((_, index) => (
          <div key={index} className="rounded-xl border border-slate-200 bg-white px-4 py-3 shadow-sm">
            <div className="h-3 w-24 rounded bg-slate-200 animate-pulse" />
            <div className="mt-2 h-4 w-32 rounded bg-slate-200 animate-pulse" />
          </div>
        ))}
      </div>
    </Panel>
  );
}

export default Profile;
