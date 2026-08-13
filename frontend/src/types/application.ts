export type JobApplication = {
    id: string;
    company: string;
    position: string;
    location: string | null;
    jobUrl: string | null;
    status: string;
    dateApplied: string | null;
    notes: string | null;
};