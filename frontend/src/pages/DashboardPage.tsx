import { useEffect, useRef } from "react";
import type { FormEvent } from "react";
import ApplicationCard from "../components/ApplicationCard";
import type { JobApplication } from "../types/application";
import ApplicationForm from "../components/ApplicationForm";
import Button from "../components/ui/Button";
import BoardView from "../components/BoardView";
import type { EmailSuggestion } from "../types/emailSuggestion";
import EmailReview from "../components/EmailReview";
import type { ApplicationEvent } from "../types/applicationEvent";
import ApplicationDetails from "../components/ApplicationDetails";


type DashboardPageProps = {
    applications: JobApplication[];
    searchQuery: string;
    statusFilter: string;
    sortOption: string;

    onSearchChange: (value: string) => void;
    onStatusFilterChange: (value: string) => void;
    onSortChange: (value: string) => void;
    viewMode: "LIST" | "BOARD" | "EMAIL";

    onViewModeChange:
        (value: "LIST" | "BOARD" | "EMAIL") => void;

    emailSuggestions: EmailSuggestion[];
    emailLoading: boolean;
    onOpenEmailReview: () => void;
    onEmailFeedback:
        (
            suggestion: EmailSuggestion,
            actualLabel: string
        ) => void;

    showForm: boolean;

    totalApplications: number;
    interviewCount: number;
    offerCount: number;
    interviewRate: number;

    company: string;
    position: string;
    location: string;
    status: string;

    onShowForm: () => void;
    onLogout: () => void;

    onCompanyChange: (value: string) => void;
    onPositionChange: (value: string) => void;
    onLocationChange: (value: string) => void;
    onStatusChange: (value: string) => void;

    onCreateApplication: (event: FormEvent<HTMLFormElement>) => void;
    onDeleteApplication: (application: JobApplication) => void;

    selectedApplication: JobApplication | null;
    applicationEvents: ApplicationEvent[];
    applicationEventsLoading: boolean;

    onOpenApplicationDetails: (application: JobApplication) => void;
    onCloseApplicationDetails: () => void;

    editingApplication: JobApplication | null;
    onEditApplication: (application: JobApplication) => void;
    onCancelEdit: () => void;

    onApproveEmailSuggestion: (suggestion: EmailSuggestion) => void;
    onCreateEmailSuggestion: (suggestion: EmailSuggestion) => void;
    onIgnoreEmailSuggestion: (suggestion: EmailSuggestion) => void;

    onBoardStatusChange: (
        application: JobApplication,
        newStatus: string
    ) => Promise<void>;
};

function DashboardPage({
                           applications,
                           searchQuery,
                           statusFilter,
                           sortOption,
                           viewMode,
                           onSearchChange,
                           onStatusFilterChange,
                           onSortChange,
                           onViewModeChange,
                           showForm,
                           emailSuggestions,
                           emailLoading,
                           onOpenEmailReview,
                           onApproveEmailSuggestion,
                           onCreateEmailSuggestion,
                           onIgnoreEmailSuggestion,
                           onEmailFeedback,
                           selectedApplication,
                           applicationEvents,
                           applicationEventsLoading,
                           onOpenApplicationDetails,
                           onCloseApplicationDetails,
                           totalApplications,
                           interviewCount,
                           offerCount,
                           interviewRate,
                           company,
                           position,
                           location,
                           status,
                           onShowForm,
                           onLogout,
                           onCompanyChange,
                           onPositionChange,
                           onLocationChange,
                           onStatusChange,
                           onCreateApplication,
                           editingApplication,
                           onEditApplication,
                           onDeleteApplication,
                           onBoardStatusChange,
                           onCancelEdit,
                       }: DashboardPageProps) {

    const formRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (showForm || editingApplication) {
            setTimeout(() => {
                formRef.current?.scrollIntoView({
                    behavior: "smooth",
                    block: "center",
                });
            }, 50);
        }
    }, [showForm, editingApplication]);

    return (
        <main className="min-h-screen bg-slate-50 px-6 py-10 text-slate-900">

            {/* Header */}
            <header className="mx-auto mb-8 max-w-6xl">
                <div>
                    <p className="text-sm font-bold uppercase tracking-[0.18em] text-indigo-600">
                        OfferBase
                    </p>

                    <h1 className="mt-3 text-4xl font-bold tracking-tight sm:text-5xl">
                        Your job search dashboard
                    </h1>

                    <p className="mt-3 max-w-2xl text-base text-slate-500">
                        Here’s how your job search is progressing.
                    </p>
                </div>

                <div className="mt-6 flex flex-col gap-4 border-b border-slate-200 pb-3 sm:flex-row sm:items-end sm:justify-between">
                    <div className="flex gap-6">
                        <button
                            type="button"
                            onClick={() => onViewModeChange("LIST")}
                            className={`border-b-2 pb-2 text-sm font-semibold transition ${
                                viewMode === "LIST"
                                    ? "border-indigo-600 text-slate-900"
                                    : "border-transparent text-slate-500 hover:text-slate-800"
                            }`}
                        >
                            List View
                        </button>

                        <button
                            type="button"
                            onClick={() => onViewModeChange("BOARD")}
                            className={`border-b-2 pb-2 text-sm font-semibold transition ${
                                viewMode === "BOARD"
                                    ? "border-indigo-600 text-slate-900"
                                    : "border-transparent text-slate-500 hover:text-slate-800"
                            }`}
                        >
                            Board View
                        </button>

                        <button
                            type="button"
                            onClick={onOpenEmailReview}
                            className={`border-b-2 pb-2 text-sm font-semibold transition ${
                                viewMode === "EMAIL"
                                    ? "border-indigo-600 text-slate-900"
                                    : "border-transparent text-slate-500 hover:text-slate-800"
                            }`}
                        >
                            Email Review
                        </button>
                    </div>

                    <div className="flex flex-wrap gap-3">
                        <Button onClick={onShowForm}>
                            + Add Application
                        </Button>

                        <Button
                            variant="secondary"
                            onClick={onLogout}
                        >
                            Log out
                        </Button>
                    </div>
                </div>
            </header>

            {/* Statistics */}
            <section className="mx-auto mb-6 grid max-w-6xl gap-4 sm:grid-cols-2 xl:grid-cols-4">

                <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                    <p className="text-sm font-medium text-slate-500">
                        Total applications
                    </p>

                    <p className="mt-3 text-3xl font-bold">
                        {totalApplications}
                    </p>

                    <p className="mt-2 text-sm text-slate-400">
                        All tracked roles
                    </p>
                </article>

                <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                    <p className="text-sm font-medium text-slate-500">
                        Interviews
                    </p>

                    <p className="mt-3 text-3xl font-bold">
                        {interviewCount}
                    </p>

                    <p className="mt-2 text-sm text-slate-400">
                        Active interview processes
                    </p>
                </article>

                <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                    <p className="text-sm font-medium text-slate-500">
                        Offers
                    </p>

                    <p className="mt-3 text-3xl font-bold">
                        {offerCount}
                    </p>

                    <p className="mt-2 text-sm text-slate-400">
                        Successful outcomes
                    </p>
                </article>

                <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                    <p className="text-sm font-medium text-slate-500">
                        Interview rate
                    </p>

                    <p className="mt-3 text-3xl font-bold">
                        {interviewRate}%
                    </p>

                    <p className="mt-2 text-sm text-slate-400">
                        Applications that led to an interview
                    </p>
                </article>

            </section>

            {/* Search / Filter / Sort */}
            <section className="mx-auto mb-6 grid max-w-6xl gap-3 lg:grid-cols-[1fr_200px_200px]">

                <input
                    className="rounded-xl border border-slate-300 bg-white px-4 py-3 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
                    type="search"
                    placeholder="Search by company or position"
                    value={searchQuery}
                    onChange={(event) =>
                        onSearchChange(event.target.value)
                    }
                />

                <select
                    className="rounded-xl border border-slate-300 bg-white px-4 py-3 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
                    value={statusFilter}
                    onChange={(event) =>
                        onStatusFilterChange(event.target.value)
                    }
                >
                    <option value="ALL">All statuses</option>
                    <option value="SAVED">Saved</option>
                    <option value="APPLIED">Applied</option>
                    <option value="INTERVIEW">Interview</option>
                    <option value="REJECTED">Rejected</option>
                    <option value="OFFER">Offer</option>
                    <option value="WITHDRAWN">Withdrawn</option>
                </select>

                <select
                    className="rounded-xl border border-slate-300 bg-white px-4 py-3 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
                    value={sortOption}
                    onChange={(event) =>
                        onSortChange(event.target.value)
                    }
                >
                    <option value="NEWEST">Newest first</option>
                    <option value="OLDEST">Oldest first</option>
                    <option value="COMPANY">Company A–Z</option>
                    <option value="STATUS">Status</option>
                </select>
            </section>

            {/* Add / Edit Form */}
            {(showForm || editingApplication) && (
                <div
                    ref={formRef}
                    className="scroll-mt-8"
                >
                    <ApplicationForm
                        company={company}
                        position={position}
                        location={location}
                        status={status}
                        onCompanyChange={onCompanyChange}
                        onPositionChange={onPositionChange}
                        onLocationChange={onLocationChange}
                        onStatusChange={onStatusChange}
                        onSubmit={onCreateApplication}
                        isEditing={editingApplication !== null}
                        onCancel={onCancelEdit}
                    />
                </div>
            )}

            {/* Applications */}
            {viewMode === "LIST" && (
                <section className="mx-auto grid max-w-6xl gap-4">
                    {applications.length === 0 ? (
                        <div className="rounded-2xl border border-dashed border-slate-300 bg-white px-6 py-14 text-center">

                            <p className="text-lg font-semibold text-slate-800">
                                No applications yet
                            </p>

                            <p className="mt-2 text-sm text-slate-500">
                                Add your first application to start tracking your search.
                            </p>

                            <div className="mt-6">
                                <Button onClick={onShowForm}>
                                    + Add Application
                                </Button>
                            </div>

                        </div>
                    ) : (
                        applications.map((application) => (
                            <ApplicationCard
                                key={application.id}
                                application={application}
                                onEdit={onEditApplication}
                                onDelete={onDeleteApplication}
                                onOpen={onOpenApplicationDetails}
                            />
                        ))
                    )}
                </section>
            )}

            {viewMode === "BOARD" && (
                <BoardView
                    applications={applications}
                    onStatusChange={onBoardStatusChange}
                />
            )}

            {viewMode === "EMAIL" && (
                <EmailReview
                    suggestions={emailSuggestions}
                    loading={emailLoading}
                    onApprove={onApproveEmailSuggestion}
                    onCreate={onCreateEmailSuggestion}
                    onIgnore={onIgnoreEmailSuggestion}
                    onFeedback={onEmailFeedback}
                />
            )}

            {selectedApplication && (
                <ApplicationDetails
                    application={selectedApplication}
                    events={applicationEvents}
                    loading={applicationEventsLoading}
                    onClose={onCloseApplicationDetails}
                />
            )}

        </main>
    );
}

export default DashboardPage;