import type { EmailSuggestion } from "../types/emailSuggestion";
import Badge from "./ui/Badge";
import { useState } from "react";

type EmailReviewProps = {
    suggestions: EmailSuggestion[];
    loading: boolean;

    onApprove: (
        suggestion: EmailSuggestion
    ) => void;

    onCreate: (
        suggestion: EmailSuggestion
    ) => void;

    onIgnore: (
        suggestion: EmailSuggestion
    ) => void;

    onFeedback: (
        suggestion: EmailSuggestion,
        actualLabel: string
    ) => void;
};

function EmailReview({
                         suggestions,
                         loading,
                         onApprove,
                         onCreate,
                         onIgnore,
                         onFeedback,
                     }: EmailReviewProps) {

                                const [
                                    correctingSuggestionId,
                                    setCorrectingSuggestionId,
                                ] = useState<string | null>(null);

    if (loading) {
        return (
            <section className="mx-auto max-w-6xl">
                <div className="rounded-2xl border border-slate-200 bg-white p-8 text-center text-slate-500">
                    Scanning Gmail...
                </div>
            </section>
        );
    }

    if (suggestions.length === 0) {
        return (
            <section className="mx-auto max-w-6xl">
                <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-10 text-center">
                    <h2 className="text-lg font-semibold text-slate-900">
                        No hiring updates found
                    </h2>

                    <p className="mt-2 text-sm text-slate-500">
                        OfferBase did not find any emails that need your attention.
                    </p>
                </div>
            </section>
        );
    }

    return (
        <section className="mx-auto grid max-w-6xl gap-4">

            {suggestions.map((suggestion) => (

                <article
                    key={suggestion.id}
                    className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm"
                >

                    <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">

                        <div>
                            <p className="text-sm font-medium text-slate-400">
                                Gmail detected
                            </p>

                            <h2 className="mt-1 text-xl font-bold text-slate-900">
                                {suggestion.company || "Unknown company"}
                            </h2>

                            {suggestion.position && (
                                <p className="mt-1 text-slate-600">
                                    {suggestion.position}
                                </p>
                            )}
                        </div>

                        <Badge
                            variant={
                                suggestion.classification.toLowerCase() as
                                    | "saved"
                                    | "applied"
                                    | "interview"
                                    | "offer"
                                    | "rejected"
                                    | "withdrawn"
                            }
                        >
                            {suggestion.classification}
                        </Badge>

                    </div>

                    {suggestion.suggestionType === "UPDATE" ? (
                        <div className="mt-5 rounded-xl bg-slate-50 p-4">

                            <p className="text-sm font-semibold text-slate-700">
                                Suggested update
                            </p>

                            <p className="mt-2 text-lg font-bold text-slate-900">
                                {suggestion.currentStatus}
                                {" → "}
                                {suggestion.suggestedStatus}
                            </p>

                            <div className="mt-3 flex flex-wrap gap-5 text-xs text-slate-500">

            <span>
                Email confidence:{" "}
                {Math.round(
                    suggestion.confidence * 100
                )}%
            </span>

                                <span>
                Application match:{" "}
                                    {Math.round(
                                        suggestion.matchConfidence * 100
                                    )}%
            </span>

                            </div>

                        </div>

                    ) : suggestion.suggestionType === "CREATE" ? (

                        <div className="mt-5 rounded-xl bg-indigo-50 p-4">

                            <p className="text-sm font-semibold text-indigo-800">
                                New application detected
                            </p>

                            <p className="mt-2 text-sm text-indigo-700">
                                OfferBase could not find an existing application for this role.
                            </p>

                            <p className="mt-3 font-semibold text-slate-900">
                                Suggested status: {suggestion.classification}
                            </p>

                        </div>

                    ) : (

                        <div className="mt-5 rounded-xl bg-amber-50 p-4">

                            <p className="text-sm font-semibold text-amber-800">
                                Needs review
                            </p>

                            <p className="mt-1 text-sm text-amber-700">
                                OfferBase could not confidently determine what to do with this email.
                            </p>

                        </div>
                    )}

                    <div className="mt-5 border-t border-slate-100 pt-4">

                        <p className="text-sm font-semibold text-slate-700">
                            {suggestion.subject}
                        </p>

                        <p className="mt-1 text-xs text-slate-400">
                            {suggestion.from}
                        </p>

                        <p className="mt-3 line-clamp-2 text-sm text-slate-500">
                            {suggestion.snippet}
                        </p>

                    </div>

                    {suggestion.suggestionType !== "REVIEW" && (
                        <div className="mt-5 flex gap-3">

                            {suggestion.suggestionType === "UPDATE" && (
                                <button
                                    type="button"
                                    onClick={() => onApprove(suggestion)}
                                    className="rounded-xl bg-indigo-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-indigo-500"
                                >
                                    Approve Update
                                </button>
                            )}

                            {suggestion.suggestionType === "CREATE" && (
                                <button
                                    type="button"
                                    onClick={() => onCreate(suggestion)}
                                    className="rounded-xl bg-indigo-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-indigo-500"
                                >
                                    Add to OfferBase
                                </button>
                            )}

                            <button
                                type="button"
                                onClick={() => onIgnore(suggestion)}
                                className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-100"
                            >
                                Ignore
                            </button>

                        </div>
                    )}

                    <div className="mt-5 border-t border-slate-100 pt-4">

                        <p className="text-sm font-semibold text-slate-700">
                            Was this classification correct?
                        </p>

                        <div className="mt-3 flex flex-wrap gap-2">

                            <button
                                type="button"
                                onClick={() =>
                                    onFeedback(
                                        suggestion,
                                        suggestion.classification
                                    )
                                }
                                className="rounded-lg border border-slate-300 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-50"
                            >
                                Correct
                            </button>

                            <button
                                type="button"
                                onClick={() =>
                                    setCorrectingSuggestionId(
                                        suggestion.id
                                    )
                                }
                                className="rounded-lg border border-slate-300 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-50"
                            >
                                Wrong
                            </button>

                        </div>

                        {correctingSuggestionId ===
                            suggestion.id && (

                                <div className="mt-4">

                                    <p className="text-xs font-semibold text-slate-500">
                                        Actual classification
                                    </p>

                                    <div className="mt-2 flex flex-wrap gap-2">

                                        {[
                                            "APPLIED",
                                            "INTERVIEW",
                                            "OFFER",
                                            "REJECTED",
                                            "OTHER",
                                        ].map((label) => (

                                            <button
                                                key={label}
                                                type="button"
                                                onClick={() => {
                                                    onFeedback(
                                                        suggestion,
                                                        label
                                                    );

                                                    setCorrectingSuggestionId(
                                                        null
                                                    );
                                                }}
                                                className="rounded-lg bg-slate-100 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-200"
                                            >
                                                {label}
                                            </button>

                                        ))}

                                    </div>

                                </div>
                            )}

                    </div>

                </article>
            ))}

        </section>
    );
}

export default EmailReview;