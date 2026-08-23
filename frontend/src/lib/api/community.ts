import { apiClient } from "@/lib/api/client";
import type {
  CommunityPostSummary,
  CommunityPostDetail,
  CommunityReply,
} from "@/lib/types/community";

export const SAMPLE_POSTS: CommunityPostSummary[] = [
  {
    id: "post-001",
    authorName: "David Kim",
    authorAvatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
    title: "Successful iPhone 14 Pro OLED & Battery DIY Repair with True Tone Transfer",
    contentSnippet: "Just finished replacing my shattered display. Used the RepairVerse guide and an EEPROM programmer to retain True Tone...",
    category: "Smartphone",
    deviceModel: "iPhone 14 Pro",
    likesCount: 38,
    repliesCount: 12,
    isSolved: true,
    createdAt: "2024-02-14",
  },
  {
    id: "post-002",
    authorName: "Sarah Jenkins",
    authorAvatar: "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
    title: "MacBook Pro 16 M1 Liquid Metal vs PTM7950 Thermal Pad Results",
    contentSnippet: "Did a side-by-side benchmark test after removing degraded factory paste. Idle temps dropped by 14°C under heavy compile workloads.",
    category: "Laptop",
    deviceModel: "MacBook Pro 16",
    likesCount: 52,
    repliesCount: 19,
    isSolved: true,
    createdAt: "2024-03-01",
  },
];

/** Fetch community posts */
export async function fetchCommunityPosts(
  category?: string,
  signal?: AbortSignal
): Promise<{ success: boolean; data?: CommunityPostSummary[]; isDemo?: boolean }> {
  let url = "/community/posts";
  if (category) url += `?category=${encodeURIComponent(category)}`;

  const result = await apiClient<CommunityPostSummary[]>(url, {
    method: "GET",
    signal,
  });

  if (result.success && result.data && Array.isArray(result.data)) {
    return { success: true, data: result.data, isDemo: false };
  }

  return { success: true, data: SAMPLE_POSTS, isDemo: true };
}

/** Fetch community post details by ID */
export async function fetchCommunityPostById(
  id: string,
  signal?: AbortSignal
): Promise<{ success: boolean; data?: CommunityPostDetail; isDemo?: boolean }> {
  const result = await apiClient<CommunityPostDetail>(`/community/posts/${id}`, {
    method: "GET",
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data, isDemo: false };
  }

  return {
    success: true,
    data: {
      id,
      authorName: "David Kim",
      authorAvatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
      title: "Successful iPhone 14 Pro OLED & Battery DIY Repair with True Tone Transfer",
      content: "Just finished replacing my shattered display. Used the RepairVerse guide and an EEPROM programmer to retain True Tone. Total repair took around 50 minutes and saved over $280 compared to Apple Store quote.",
      category: "Smartphone",
      deviceModel: "iPhone 14 Pro",
      likesCount: 38,
      repliesCount: 2,
      isSolved: true,
      createdAt: "2024-02-14",
      replies: [
        {
          id: "rep-1",
          authorName: "Elena Rostova",
          authorAvatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
          content: "Great job preserving the ambient light sensor! Did you apply the pre-cut waterproof gasket as well?",
          isSolution: false,
          likesCount: 6,
          createdAt: "2024-02-14T14:30:00",
        },
      ],
    },
    isDemo: true,
  };
}

/** Create a new community discussion post */
export async function createCommunityPost(
  token: string,
  data: { title: string; content: string; category: string; deviceModel?: string },
  signal?: AbortSignal
): Promise<{ success: boolean; data?: CommunityPostDetail; message?: string }> {
  const result = await apiClient<CommunityPostDetail>("/community/posts", {
    method: "POST",
    token,
    body: data,
    signal,
  });

  return {
    success: result.success,
    data: result.data,
    message: result.message,
  };
}

/** Like a community post */
export async function likeCommunityPost(
  postId: string,
  signal?: AbortSignal
): Promise<{ success: boolean; likesCount?: number }> {
  const result = await apiClient<{ likesCount: number }>(`/community/posts/${postId}/like`, {
    method: "POST",
    signal,
  });

  return {
    success: result.success,
    likesCount: result.data?.likesCount,
  };
}
