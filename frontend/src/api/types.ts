export type ApiError = {
  code: string;
  message: string;
};

export type Profile = {
  displayName?: string | null;
  phoneNumber?: string | null;
  company?: string | null;
};

export type Account = {
  id: number;
  email: string;
  status: "ACTIVE" | "SUSPENDED";
  profile?: Profile | null;
  profileComplete: boolean;
};
