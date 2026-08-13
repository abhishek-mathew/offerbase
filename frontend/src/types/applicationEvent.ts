export type ApplicationEvent = {
    id: string;
    eventType: string;
    status: string | null;
    description: string | null;
    source: string;
    createdAt: string;
};