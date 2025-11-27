export type RegionCode = "HKI" | "KL" | "NT" | "LANTAU" | "ISLANDS";

export const REGION_LABELS: Record<RegionCode, string> = {
  HKI: "Hong Kong Island",
  KL: "Kowloon",
  NT: "New Territories",
  LANTAU: "Lantau Island",
  ISLANDS: "Other Islands",
};

const REGION_BY_DISTRICT: Record<string, RegionCode> = {
  "central and western": "HKI",
  "wan chai": "HKI",
  "eastern": "HKI",
  "southern": "HKI",
  "kowloon city": "KL",
  "wong tai sin": "KL",
  "yau tsim mong": "KL",
  "sham shui po": "KL",
  "kwun tong": "KL",
  "sai kung": "NT",
  "shatin": "NT",
  "sha tin": "NT",
  "tai po": "NT",
  "north": "NT",
  "tsuen wan": "NT",
  "tuen mun": "NT",
  "yuen long": "NT",
  "kwai tsing": "NT",
  "islands": "ISLANDS",
};

export function getRegionForHospital(district?: string, hospitalName?: string): RegionCode {
  if (!district && !hospitalName) {
    return "NT";
  }

  const normalizedDistrict = district?.toLowerCase().trim();
  if (normalizedDistrict) {
    const byDistrict = REGION_BY_DISTRICT[normalizedDistrict];
    if (byDistrict && byDistrict !== "ISLANDS") {
      return byDistrict;
    }

    if (byDistrict === "ISLANDS") {
      if (hospitalName?.toLowerCase().includes("lantau")) {
        return "LANTAU";
      }
      return "ISLANDS";
    }
  }

  if (hospitalName?.toLowerCase().includes("lantau")) {
    return "LANTAU";
  }

  return "NT";
}

