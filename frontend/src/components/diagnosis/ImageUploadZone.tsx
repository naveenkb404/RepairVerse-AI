"use client";

import { useState, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Upload, Image as ImageIcon, X, AlertCircle, CheckCircle2, RefreshCw } from "lucide-react";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

export type ImageUploadZoneProps = {
  onImageSelected: (file: File | null, previewUrl: string | null) => void;
  selectedImagePreview: string | null;
  selectedFileName: string | null;
  className?: string;
};

const ALLOWED_TYPES = ["image/jpeg", "image/png", "image/webp"];
const MAX_SIZE_MB = 10;
const MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024;

export default function ImageUploadZone({
  onImageSelected,
  selectedImagePreview,
  selectedFileName,
  className,
}: ImageUploadZoneProps) {
  const [isDragging, setIsDragging] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const processFile = (file: File) => {
    setErrorMessage(null);

    if (!ALLOWED_TYPES.includes(file.type)) {
      setErrorMessage("Unsupported file format. Please upload a JPG, PNG, or WEBP image.");
      return;
    }

    if (file.size > MAX_SIZE_BYTES) {
      setErrorMessage(`File size exceeds ${MAX_SIZE_MB}MB limit. Please choose a smaller image.`);
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      const previewUrl = e.target?.result as string;
      onImageSelected(file, previewUrl);
    };
    reader.readAsDataURL(file);
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(false);

    const files = e.dataTransfer.files;
    if (files && files.length > 0) {
      processFile(files[0]);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      processFile(files[0]);
    }
  };

  const handleRemove = () => {
    setErrorMessage(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
    onImageSelected(null, null);
  };

  return (
    <div className={cn("space-y-3", className)}>
      <label className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1]">
        Upload Device Image <span className="text-[#22C55E]">*</span>
      </label>

      {/* Upload Box */}
      <div
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        onClick={() => !selectedImagePreview && fileInputRef.current?.click()}
        className={`relative flex min-h-[220px] flex-col items-center justify-center rounded-3xl border-2 border-dashed p-6 text-center transition-all ${
          selectedImagePreview
            ? "border-[#22C55E]/40 bg-white/[0.04]"
            : isDragging
            ? "border-[#22C55E] bg-[#22C55E]/10 scale-[1.01]"
            : errorMessage
            ? "border-red-500/50 bg-red-500/[0.04]"
            : "border-white/20 bg-white/[0.03] hover:border-[#22C55E]/50 hover:bg-white/[0.05] cursor-pointer"
        }`}
      >
        <input
          ref={fileInputRef}
          type="file"
          accept="image/jpeg,image/png,image/webp"
          onChange={handleFileChange}
          className="hidden"
          aria-label="Upload device image file"
        />

        <AnimatePresence mode="wait">
          {selectedImagePreview ? (
            <motion.div
              key="preview"
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              transition={{ duration: 0.3, ease: EASE }}
              className="flex flex-col items-center justify-center w-full"
            >
              <div className="relative mb-3 group/preview">
                <img
                  src={selectedImagePreview}
                  alt="Uploaded device preview"
                  className="max-h-48 rounded-2xl border border-white/20 object-contain shadow-lg"
                />
                <div className="absolute inset-0 rounded-2xl bg-black/40 opacity-0 group-hover/preview:opacity-100 transition-opacity flex items-center justify-center gap-2">
                  <button
                    type="button"
                    onClick={(e) => {
                      e.stopPropagation();
                      fileInputRef.current?.click();
                    }}
                    className="flex size-9 items-center justify-center rounded-full bg-white/20 text-white backdrop-blur-md hover:bg-white/30"
                    title="Replace Image"
                  >
                    <RefreshCw className="size-4" />
                  </button>
                  <button
                    type="button"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleRemove();
                    }}
                    className="flex size-9 items-center justify-center rounded-full bg-red-500/80 text-white backdrop-blur-md hover:bg-red-600"
                    title="Remove Image"
                  >
                    <X className="size-4" />
                  </button>
                </div>
              </div>

              <div className="flex items-center gap-2 text-xs text-[#22C55E] font-medium">
                <CheckCircle2 className="size-4" />
                <span className="truncate max-w-[240px] text-white">{selectedFileName || "Image loaded"}</span>
              </div>
            </motion.div>
          ) : (
            <motion.div
              key="empty"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="flex flex-col items-center justify-center"
            >
              <div className="mb-3 flex size-14 items-center justify-center rounded-2xl border border-white/15 bg-white/[0.08] text-[#22C55E] shadow-sm">
                <Upload className="size-7" aria-hidden />
              </div>

              <p className="text-sm font-semibold text-white">
                Drag & drop device photo here, or{" "}
                <span className="text-[#22C55E] underline underline-offset-4">browse file</span>
              </p>

              <p className="mt-1 text-xs text-white/50">
                Supports JPG, PNG, WEBP up to 10MB
              </p>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* Error Alert */}
      {errorMessage && (
        <motion.div
          initial={{ opacity: 0, y: -6 }}
          animate={{ opacity: 1, y: 0 }}
          className="flex items-center gap-2 text-xs text-red-400 font-medium"
        >
          <AlertCircle className="size-4 shrink-0" />
          <span>{errorMessage}</span>
        </motion.div>
      )}
    </div>
  );
}
