import type { JobApplication } from "../types/application";
import type { ApplicationEvent } from "../types/applicationEvent";

type ApplicationDetailsProps = {
    application: JobApplication;
    events: ApplicationEvent[];
    loading: boolean;
    onClose: () => void;
};

export default function ApplicationDetails({
                                               application,
                                               events,
                                               loading,
                                               onClose,
                                           }: ApplicationDetailsProps) {

    return (
        <div className="fixed inset-0 z-50 flex justify-end bg-black/30">

            <div className="h-full w-full max-w-xl overflow-y-auto bg-white p-8 shadow-xl">

                <div className="flex items-start justify-between gap-4">

                    <div>
                        <p className="text-sm font-semibold uppercase tracking-wide text-slate-500">
                            {application.company}
                        </p>

                        <h2 className="mt-1 text-2xl font-bold text-slate-900">
                            {application.position}
                        </h2>

                        {application.location && (
                            <p className="mt-2 text-sm text-slate-500">
                                {application.location}
                            </p>
                        )}
                    </div>

                    <button
                        type="button"
                        onClick={onClose}
                        className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-50"
                    >
                        Close
                    </button>

                </div>

                <div className="mt-6 rounded-xl border border-slate-200 p-4">

                    <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                        Current status
                    </p>

                    <p className="mt-1 text-lg font-bold text-slate-900">
                        {application.status}
                    </p>

                    {application.dateApplied && (
                        <p className="mt-2 text-sm text-slate-500">
                            Applied {application.dateApplied}
                        </p>
                    )}

                </div>

                <div className="mt-8">

                    <h3 className="text-lg font-bold text-slate-900">
                        Timeline
                    </h3>

                    {loading ? (

                        <p className="mt-4 text-sm text-slate-500">
                            Loading history...
                        </p>

                    ) : events.length === 0 ? (

                        <p className="mt-4 text-sm text-slate-500">
                            No history recorded for this application yet.
                        </p>

                    ) : (

                        <div className="mt-5 space-y-6">

                            {events.map((event) => (

                                <div
                                    key={event.id}
                                    className="border-l-2 border-slate-200 pl-5"
                                >

                                    <p className="text-sm font-semibold text-slate-900">
                                        {event.eventType === "CREATED"
                                            ? "Application created"
                                            : event.eventType === "STATUS_CHANGED"
                                                ? "Status changed"
                                                : event.eventType}
                                    </p>

                                    {event.description && (
                                        <p className="mt-1 text-sm text-slate-600">
                                            {event.description}
                                        </p>
                                    )}

                                    <div className="mt-2 flex gap-3 text-xs text-slate-400">

                                        <span>
                                            {new Date(
                                                event.createdAt
                                            ).toLocaleString()}
                                        </span>

                                        <span>
                                            {event.source}
                                        </span>

                                    </div>

                                </div>

                            ))}

                        </div>
                    )}

                </div>

                {application.notes && (
                    <div className="mt-8">

                        <h3 className="text-lg font-bold text-slate-900">
                            Notes
                        </h3>

                        <p className="mt-3 whitespace-pre-wrap text-sm text-slate-600">
                            {application.notes}
                        </p>

                    </div>
                )}

                {application.jobUrl && (
                    <a
                        href={application.jobUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="mt-8 inline-block text-sm font-semibold text-blue-600 hover:underline"
                    >
                        View job posting
                    </a>
                )}

            </div>

        </div>
    );
}