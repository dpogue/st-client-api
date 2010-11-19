routes = dict(

    push                 = '/data/push',

    signals              = '/data/signals',
    signal               = '/data/signals/%(hash_or_id)s',

    user                 = '/data/users/%(user_id)s',
    usergroups           = '/data/users/%(user_id)s/groups',
    users                = '/data/users',

    version              = '/data/version',

    backlinks            = '/data/workspaces/%(ws)s/pages/%(pname)s/backlinks',
    breadcrumbs          = '/data/workspaces/%(ws)s/breadcrumbs',
    frontlinks           = '/data/workspaces/%(ws)s/pages/%(pname)/frontlinks',
    page                 = '/data/workspaces/%(ws)s/pages/%(pname)s',
    pages                = '/data/workspaces/%(ws)s/pages',
    pagetag              = '/data/workspaces/%(ws)s/pages/%(pname)s/tags/%(tag)s',
    pagetags             = '/data/workspaces/%(ws)s/pages/%(pname)s/tags',
    pagecomments         = '/data/workspaces/%(ws)s/pages/%(pname)s/comments',
    pageattachment       = '/data/workspaces/%(ws)s/pages/%(pname)s/attachments/%(attachment_id)s',
    pageattachments      = '/data/workspaces/%(ws)s/pages/%(pname)s/attachments',
    taggedpages          = '/data/workspaces/%(ws)s/tags/%(tag)s/pages',
    workspace            = '/data/workspaces/%(ws)s',
    workspaces           = '/data/workspaces',
    workspacetag         = '/data/workspaces/%(ws)s/tags/%(tag)s',
    workspacetags        = '/data/workspaces/%(ws)s/tags',
    workspaceattachment  = '/data/workspaces/%(ws)s/attachments/%(attachment_id)s',
    workspaceattachments = '/data/workspaces/%(ws)s/attachments',
    workspaceuser        = '/data/workspaces/%(ws)s/users/%(user_id)s',
    workspaceusers       = '/data/workspaces/%(ws)s/users',
)

