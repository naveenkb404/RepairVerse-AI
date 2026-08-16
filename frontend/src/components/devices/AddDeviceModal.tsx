"use client";

import { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { X, Plus, Smartphone, Loader2, AlertCircle } from "lucide-react";
import { CreateDeviceRequest } from "@/lib/types/device";
import GlassButton from "@/components/common/GlassButton";

const EASE = [0.22, 1, 0.36, 1] as const;

type AddDeviceModalProps = {
  isOpen: boolean;
  onClose: () => void;
  onAddDevice: (device: CreateDeviceRequest) => Promise<boolean>;
};

const CATEGORIES = [
  "Smartphone",
  "Laptop",
  "Tablet",
  "Gaming Console",
  "Smartwatch",
  "Audio Device",
  "Other",
];

const CONDITIONS = ["Excellent", "Good", "Fair", "Needs Attention", "Needs Repair"];

export default function AddDeviceModal({
  isOpen,
  onClose,
  onAddDevice,
}: AddDeviceModalProps) {
  const [formData, setFormData] = useState<CreateDeviceRequest>({
    deviceName: "",
    category: "Smartphone",
    brand: "",
    model: "",
    serialNumber: "",
    purchaseDate: "",
    warrantyExpiry: "",
    purchasePrice: undefined,
    currentCondition: "Good",
  });

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape" && isOpen) {
        onClose();
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, onClose]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.deviceName || !formData.brand || !formData.model) {
      setErrorMsg("Device Name, Brand, and Model are required.");
      return;
    }

    setIsSubmitting(true);
    setErrorMsg("");

    const success = await onAddDevice(formData);
    setIsSubmitting(false);

    if (success) {
      setFormData({
        deviceName: "",
        category: "Smartphone",
        brand: "",
        model: "",
        serialNumber: "",
        purchaseDate: "",
        warrantyExpiry: "",
        purchasePrice: undefined,
        currentCondition: "Good",
      });
      onClose();
    } else {
      setErrorMsg("Failed to add device. Please try again.");
    }
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="absolute inset-0 bg-black/80 backdrop-blur-md"
          />

          <motion.div
            initial={{ opacity: 0, scale: 0.95, y: 16 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: 16 }}
            transition={{ duration: 0.3, ease: EASE }}
            className="relative w-full max-w-lg overflow-hidden rounded-3xl border border-white/15 bg-[#0d1526] p-6 shadow-2xl backdrop-blur-2xl max-h-[90vh] overflow-y-auto"
          >
            <div className="flex items-center justify-between border-b border-white/10 pb-4">
              <div className="flex items-center gap-2">
                <div className="flex size-8 items-center justify-center rounded-xl bg-[#22C55E]/20 text-[#22C55E]">
                  <Plus className="size-4" />
                </div>
                <h3 className="text-base font-bold text-white">
                  Add Device to Health Passport
                </h3>
              </div>
              <button
                type="button"
                onClick={onClose}
                className="rounded-full border border-white/10 bg-white/5 p-1.5 text-white/60 hover:text-white transition-colors"
                aria-label="Close dialog"
              >
                <X className="size-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="mt-5 space-y-4">
              {errorMsg && (
                <div className="flex items-center gap-2 rounded-2xl border border-red-500/30 bg-red-500/10 p-3 text-xs text-red-300">
                  <AlertCircle className="size-4 shrink-0 text-red-400" />
                  {errorMsg}
                </div>
              )}

              {/* Device Name */}
              <div>
                <label className="block text-xs font-semibold text-white/80 mb-1">
                  Device Name / Nickname *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Personal iPhone, Living Room Console"
                  value={formData.deviceName}
                  onChange={(e) =>
                    setFormData({ ...formData, deviceName: e.target.value })
                  }
                  className="w-full rounded-2xl border border-white/15 bg-white/[0.05] p-3 text-xs font-medium text-white placeholder-white/30 backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
                />
              </div>

              {/* Category & Condition */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-white/80 mb-1">
                    Category *
                  </label>
                  <select
                    value={formData.category}
                    onChange={(e) =>
                      setFormData({ ...formData, category: e.target.value })
                    }
                    className="w-full rounded-2xl border border-white/15 bg-[#0B1120] p-3 text-xs font-medium text-white focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
                  >
                    {CATEGORIES.map((cat) => (
                      <option key={cat} value={cat}>
                        {cat}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-white/80 mb-1">
                    Current Condition *
                  </label>
                  <select
                    value={formData.currentCondition}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        currentCondition: e.target.value,
                      })
                    }
                    className="w-full rounded-2xl border border-white/15 bg-[#0B1120] p-3 text-xs font-medium text-white focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
                  >
                    {CONDITIONS.map((cond) => (
                      <option key={cond} value={cond}>
                        {cond}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Brand & Model */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-white/80 mb-1">
                    Brand *
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Apple, Sony, Dell"
                    value={formData.brand}
                    onChange={(e) =>
                      setFormData({ ...formData, brand: e.target.value })
                    }
                    className="w-full rounded-2xl border border-white/15 bg-white/[0.05] p-3 text-xs font-medium text-white placeholder-white/30 backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-white/80 mb-1">
                    Model *
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. iPhone 14 Pro, PS5"
                    value={formData.model}
                    onChange={(e) =>
                      setFormData({ ...formData, model: e.target.value })
                    }
                    className="w-full rounded-2xl border border-white/15 bg-white/[0.05] p-3 text-xs font-medium text-white placeholder-white/30 backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
                  />
                </div>
              </div>

              {/* Serial Number & Price */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-white/80 mb-1">
                    Serial Number (Optional)
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. F2LX9001K992"
                    value={formData.serialNumber || ""}
                    onChange={(e) =>
                      setFormData({ ...formData, serialNumber: e.target.value })
                    }
                    className="w-full rounded-2xl border border-white/15 bg-white/[0.05] p-3 text-xs font-medium text-white placeholder-white/30 backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50 font-mono"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-white/80 mb-1">
                    Purchase Price ($ Optional)
                  </label>
                  <input
                    type="number"
                    min="0"
                    placeholder="e.g. 999"
                    value={formData.purchasePrice ?? ""}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        purchasePrice: e.target.value
                          ? Number(e.target.value)
                          : undefined,
                      })
                    }
                    className="w-full rounded-2xl border border-white/15 bg-white/[0.05] p-3 text-xs font-medium text-white placeholder-white/30 backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
                  />
                </div>
              </div>

              {/* Purchase Date & Warranty Expiry */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-white/80 mb-1">
                    Purchase Date (Optional)
                  </label>
                  <input
                    type="date"
                    value={formData.purchaseDate || ""}
                    onChange={(e) =>
                      setFormData({ ...formData, purchaseDate: e.target.value })
                    }
                    className="w-full rounded-2xl border border-white/15 bg-white/[0.05] p-3 text-xs font-medium text-white backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-white/80 mb-1">
                    Warranty Expiry (Optional)
                  </label>
                  <input
                    type="date"
                    value={formData.warrantyExpiry || ""}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        warrantyExpiry: e.target.value,
                      })
                    }
                    className="w-full rounded-2xl border border-white/15 bg-white/[0.05] p-3 text-xs font-medium text-white backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
                  />
                </div>
              </div>

              <div className="flex items-center gap-3 pt-4 border-t border-white/10">
                <GlassButton
                  type="button"
                  variant="secondary"
                  size="sm"
                  fullWidth
                  onClick={onClose}
                >
                  Cancel
                </GlassButton>
                <GlassButton
                  type="submit"
                  size="sm"
                  fullWidth
                  disabled={isSubmitting}
                  icon={
                    isSubmitting ? (
                      <Loader2 className="size-3.5 animate-spin" />
                    ) : (
                      <Smartphone className="size-3.5" />
                    )
                  }
                >
                  {isSubmitting ? "Adding Device..." : "Save Device"}
                </GlassButton>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
}
