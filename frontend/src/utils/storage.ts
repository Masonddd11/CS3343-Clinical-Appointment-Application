const TOKEN_KEY = "token";
const USER_KEY = "user";

export const storage = {
  getToken: (): string | null => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem(TOKEN_KEY);
  },

  setToken: (token: string): void => {
    if (typeof window === "undefined") return;
    localStorage.setItem(TOKEN_KEY, token);
  },

  removeToken: (): void => {
    if (typeof window === "undefined") return;
    localStorage.removeItem(TOKEN_KEY);
  },

  getUser: <T>(): T | null => {
    if (typeof window === "undefined") return null;
    const user = localStorage.getItem(USER_KEY);
    if (!user) return null;
    try {
      return JSON.parse(user) as T;
    } catch {
      return null;
    }
  },

  setUser: <T>(user: T): void => {
    if (typeof window === "undefined") return;
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },

  removeUser: (): void => {
    if (typeof window === "undefined") return;
    localStorage.removeItem(USER_KEY);
  },

  clear: (): void => {
    storage.removeToken();
    storage.removeUser();
  },
};
