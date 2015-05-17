package logic

sealed trait Resource { def name: String }
case object BRANCH extends Resource { val name = "Branch" }
case object COMMIT extends Resource { val name = "Commit" }
case object CONNEXION extends Resource { val name = "Connexion" }
case object DEPLOY_KEY extends Resource { val name = "Deploy Key" }
case object FILE extends Resource { val name = "File" }
case object GROUP extends Resource { val name = "Group" }
case object PROJECT_HOOK extends Resource { val name = "Project Hook" }
case object SYSTEM_HOOK extends Resource { val name = "System Hook" }
case object ISSUE_NOTE extends Resource { val name = "Issue Note" }
case object ISSUE extends Resource { val name = "Issue" }
case object LABEL extends Resource { val name = "Label" }
case object MERGE_REQUEST_NOTE extends Resource { val name = "Merge Request Note" }
case object MERGE_REQUEST extends Resource { val name = "Merge Request" }
case object MILESTONE extends Resource { val name = "Milestone" }
case object PROJECT extends Resource { val name = "Project" }
case object REPOSITORY extends Resource { val name = "Repository" }
case object SNIPPET_NOTE extends Resource { val name = "Snippet Note" }
case object SNIPPET extends Resource { val name = "Snippet" }
case object SSH_KEY extends Resource { val name = "SSH Key" }
case object TEAM extends Resource { val name = "Team" }
case object TEAM_MEMBER extends Resource { val name = "Team Member" }
case object USER extends Resource { val name = "User" }