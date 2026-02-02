const DELETE_ICON_BASE64 = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTYiIGhlaWdodD0iMTYiIHZpZXdCb3g9IjAgMCAxNiAxNiIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICAgIDxwYXRoIGQ9Im0xMy4xNTMgMiAuODQ3Ljg0N0w4Ljg0NiA4IDE0IDEzLjE1M2wtLjg0Ny44NDdMOCA4Ljg0NiAyLjg0NyAxNCAyIDEzLjE1MyA3LjE1MyA4IDIgMi44NDcgMi44NDcgMiA4IDcuMTUzIDEzLjE1MyAyeiIgZmlsbD0iIzQ0ODk5MyIgZmlsbC1ydWxlPSJub256ZXJvIi8+Cjwvc3ZnPgo='

const DELETE_ICON = createImageElement(DELETE_ICON_BASE64)

function createImageElement(src) {
    const img = document.createElement('img')

    img.src = src

    return img
}

function deleteObject(_, transform) {
    const target = transform.target
    const canvas = target.canvas

    canvas.remove(target)
    canvas.requestRenderAll()
}

function RectRenderer(ctx) {
    return (width, height) => {
        ctx.rect(-width / 2, -height / 2, width, height)
        ctx.stroke()

        ctx.fillStyle = '#ffffff'
        ctx.fillRect(-width / 2, -height / 2, width, height)
    }
}

function renderCorner(ctx, left, top) {
    const renderRect = RectRenderer(ctx)

    ctx.save()
    ctx.translate(left, top)

    renderRect(8, 8)

    ctx.restore()
}

export async function setup() {
    const { fabric } = await import('fabric')

    const fabricDefaults = fabric.Object.prototype

    fabricDefaults.controls.mtr = new fabric.Control({
        x: 0.5,
        y: 0,
        mouseUpHandler: deleteObject,
        cursorStyle: 'pointer',
        offsetX: 30,
        cornerSize: 16,
        render: function (ctx, left, top, styleOverride, fabricObject) {
            const size = this.cornerSize
            const renderRect = RectRenderer(ctx)

            ctx.save()
            ctx.translate(left, top)

            renderRect(24, 24)

            ctx.drawImage(DELETE_ICON, -size / 2, -size / 2, size, size)
            ctx.restore()
        },
        withConnection: true,
    })

    fabricDefaults.controls.tl = new fabric.Control({
        ...fabricDefaults.controls.tl,
        render: renderCorner,
    })

    fabricDefaults.controls.mt = new fabric.Control({
        ...fabricDefaults.controls.mt,
        render: renderCorner,
    })

    fabricDefaults.controls.tr = new fabric.Control({
        ...fabricDefaults.controls.tr,
        render: renderCorner,
    })

    fabricDefaults.controls.mr = new fabric.Control({
        ...fabricDefaults.controls.mr,
        render: renderCorner,
    })

    fabricDefaults.controls.br = new fabric.Control({
        ...fabricDefaults.controls.br,
        render: renderCorner,
    })

    fabricDefaults.controls.mb = new fabric.Control({
        ...fabricDefaults.controls.mb,
        render: renderCorner,
    })

    fabricDefaults.controls.bl = new fabric.Control({
        ...fabricDefaults.controls.bl,
        render: renderCorner,
    })

    fabricDefaults.controls.ml = new fabric.Control({
        ...fabricDefaults.controls.ml,
        render: renderCorner,
    })

    fabricDefaults.cornerSize = 8
    fabricDefaults.cornerColor = '#0064ad'
}