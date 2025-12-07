import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "./client";
import type { Account, ApiError, Profile } from "./types";
import { AxiosError } from "axios";

export const useMe = () =>
  useQuery<Account, AxiosError<ApiError>>({
    queryKey: ["me"],
    queryFn: async () => {
      const { data } = await api.get<Account>("/me");
      return data;
    },
    retry: false,
  });

export const useUpdateProfile = () => {
  const queryClient = useQueryClient();

  return useMutation<Account, AxiosError<ApiError>, Partial<Profile>>({
    mutationFn: async (body) => {
      const { data } = await api.put<Account>("/profile", body);
      return data;
    },
    onSuccess: (data) => {
      queryClient.setQueryData(["me"], data);
    },
  });
};
