import csv
import struct

node_icons = ["Default", "Season", "Chapter", "Star", "Shine", "Crown",
              "ToadBrigade", "GhostPlayer", "GhostPlayerMaze", "Bonus"]

stage_types = ["Prologue", "Illustration", "IllustrationDemo", "MiniGame", "TrickArt", "Normal", "Bonus",
               "Special3DWorld", "SpecialCherry", "SpecialManek", "SpecialPrologue", "SpecialBonus", "SpecialOther",
               "Labyrinth"]

# prepare buffers
strings = []
entries = []

# read entries from StageNodeInfo.csv and collect stage names
with open("src/assets/bin/StageNodeInfo.csv", encoding="utf-8") as f:
    reader = csv.DictReader(f, delimiter=";")

    for entry in reader:
        if entry["StageName"] not in strings:
            strings.append(entry["StageName"])
        entries.append(entry)

# pack strings
out_strings = bytearray()
strings.sort()

for string in strings:
    encoded = string.encode("utf-8")
    out_strings += bytearray(struct.pack(">H", len(encoded)))
    out_strings += bytearray(encoded)

# pack entries
out = bytearray(16)
out_data = bytearray()
num_entries = 0

for entry in entries:
    name_idx = strings.index(entry["StageName"])
    course_id = int(entry["CourseId"])
    stage_type = stage_types.index(entry["StageType"]) if entry["StageType"] in stage_types else -1
    node_depth = int(entry["NodeDepth"]) & 0xF
    node_icon = node_icons.index(entry["NodeIcon"])
    node_info = (node_icon << 4) | node_depth
    game_version = int(entry["GameVersion"])
    challenge_time = int(entry["ChallengeTime"])

    def get_flag(name, mask):
        return mask if entry[name] == "WAHR" else 0

    flags = get_flag("HasDotKinopio", 1)
    flags |= get_flag("HasCollectItem", 2)
    flags |= get_flag("HasBadgeCondition", 4)
    flags |= get_flag("HasChallengeTime", 8)
    flags |= get_flag("HasDlcCollectItem", 16)
    flags |= get_flag("HasPreviewImage", 32)

    out_entry = struct.pack(">HhbBBBH", name_idx, course_id, stage_type, node_info, game_version, flags, challenge_time)
    out_data += bytearray(out_entry)
    num_entries += 1

out += out_strings + out_data
struct.pack_into(">IIII", out, 0, num_entries, len(strings), len(out_strings), len(out))

with open("src/assets/bin/StageNodeInfo.bin", "wb") as f:
    f.write(out)
    f.flush()
