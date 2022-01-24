import csv
import struct

# Define enum types -> will be converted to int using indices
ICONS = [
    "Default", "Season", "Chapter", "Star", "Shine", "Crown", "ToadBrigade", "GhostPlayer", "GhostPlayerMaze", "Bonus"
]
TYPES = [
    "Season", "Prologue", "Illustration", "IllustrationDemo", "MiniGame", "TrickArt", "Normal", "Bonus",
    "Special3DWorld", "SpecialCherry", "SpecialManek", "SpecialPrologue", "SpecialBonus", "SpecialOther", "Labyrinth"
]


def trymask(e: dict, k: str, m: int) -> int: return m if e[k].lower() == "wahr" else 0


# Prepare buffers
strings = []
entries = []

# Read entries from StageNodeInfo.csv and collect stage names
with open("src/assets/bin/StageNodeInfo.csv", encoding="utf-8") as f:
    reader = csv.DictReader(f, delimiter=";")

    for entry in reader:
        if entry["StageName"] not in strings:
            strings.append(entry["StageName"])
        entries.append(entry)

# Pack strings
out_strings = bytearray()
strings.sort()

for string in strings:
    encoded = string.encode("utf-8")
    out_strings += struct.pack("<H", len(encoded))
    out_strings += encoded

# Pack entries
out = bytearray(16)
out_data = bytearray()
num_entries = 0

for entry in entries:
    # Values from CSV
    name_idx = strings.index(entry["StageName"])
    course_id = int(entry["CourseId"])
    stage_type = TYPES.index(entry["StageType"]) if entry["StageType"] in TYPES else -1
    page_id = int(entry["PageId"])
    node_depth = int(entry["NodeDepth"]) & 0xF
    node_icon = ICONS.index(entry["NodeIcon"]) & 0xF
    collect_item_num = int(entry["CollectItemNum"]) & 0xF
    challenge_time = int(entry["ChallengeTime"])
    game_version = int(entry["GameVersion"]) & 0xF

    # Pack nybbles
    node_info = (node_icon << 4) | node_depth
    item_and_version = (collect_item_num << 4) | game_version

    # Pack flags
    flags = trymask(entry, "HasDotKinopio", 1)
    flags |= trymask(entry, "HasDlcCollectItem", 2)
    flags |= trymask(entry, "HasBadgeCondition", 4)
    flags |= trymask(entry, "HasChallengeTime", 8)
    flags |= trymask(entry, "IsVRStage", 16)
    flags |= trymask(entry, "HasPreviewImage", 32)

    out_entry = struct.pack("<HhhbBBBH", name_idx, course_id, page_id, stage_type, node_info, item_and_version, flags, challenge_time)
    out_data += bytearray(out_entry)
    num_entries += 1

# Join buffers and pack header
out += out_strings + out_data
struct.pack_into("<4I", out, 0, num_entries, len(strings), len(out_strings), len(out))

with open("src/assets/bin/StageNodeInfo.bin", "wb") as f:
    f.write(out)
    f.flush()
