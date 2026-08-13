import type { FormEvent } from "react";

type LoginPageProps = {
    email: string;
    password: string;
    message: string;
    loading: boolean;
    onEmailChange: (value: string) => void;
    onPasswordChange: (value: string) => void;
    onSubmit: (event: FormEvent<HTMLFormElement>) => void;
};

function LoginPage({
                       email,
                       password,
                       message,
                       loading,
                       onEmailChange,
                       onPasswordChange,
                       onSubmit,
                   }: LoginPageProps) {
    return (
        <main className="min-h-screen bg-slate-950 px-6 py-12 text-slate-100">
            <div className="mx-auto grid min-h-[calc(100vh-6rem)] max-w-6xl items-center gap-12 lg:grid-cols-2">
                <section>
                    <p className="mb-4 text-sm font-bold uppercase tracking-[0.2em] text-indigo-400">
                        OfferBase
                    </p>

                    <h1 className="max-w-xl text-5xl font-bold leading-tight sm:text-6xl">
                        Stay on top of your job search.
                    </h1>

                    <p className="mt-6 max-w-lg text-lg leading-8 text-slate-400">
                        Every application. Every interview. One place.
                    </p>
                </section>

                <section className="rounded-3xl border border-slate-800 bg-slate-900/80 p-8 shadow-2xl shadow-black/30 backdrop-blur sm:p-10">
                    <h2 className="text-3xl font-bold">Welcome back</h2>

                    <p className="mt-2 text-slate-400">
                        Sign in to continue to your dashboard.
                    </p>

                    <form className="mt-8 grid gap-5" onSubmit={onSubmit}>
                        <label className="grid gap-2 text-sm font-semibold">
                            Email
                            <input
                                className="rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-slate-100 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/15"
                                type="email"
                                value={email}
                                onChange={(event) => onEmailChange(event.target.value)}
                                required
                            />
                        </label>

                        <label className="grid gap-2 text-sm font-semibold">
                            Password
                            <input
                                className="rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-slate-100 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/15"
                                type="password"
                                value={password}
                                onChange={(event) => onPasswordChange(event.target.value)}
                                required
                            />
                        </label>

                        <button
                            className="mt-2 rounded-xl bg-indigo-500 px-4 py-3 font-bold text-white transition hover:bg-indigo-400 disabled:cursor-not-allowed disabled:opacity-60"
                            type="submit"
                            disabled={loading}
                        >
                            {loading ? "Signing in..." : "Sign in"}
                        </button>
                    </form>

                    {message && (
                        <p className="mt-5 text-center text-sm font-semibold text-rose-400">
                            {message}
                        </p>
                    )}
                </section>
            </div>
        </main>
    );
}

export default LoginPage;