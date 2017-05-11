function distance(from, to) {
  return Math.sqrt(Math.pow(from[0] - to[0], 2) + Math.pow(from[1] - to[1], 2));
}

class GeoDrawer {
  constructor(ctx, options) {
    this.context = ctx;
    this.realCenter = options.realCenter;
    this.canvasCenter = options.canvasCenter;
    this.scale = distance(options.canvasFrom, options.canvasTo)
      / distance(options.realFrom, options.realTo);
  }

  drawPoint(p) {  // [latitude (y val), longitude (x val)]
    const canvasX = this.canvasCenter[0] + this.scale * (p[1] - this.realCenter[1]);
    const canvasY = this.canvasCenter[1] - this.scale * (p[0] - this.realCenter[0]);

    console.log(p[0], p[1]);
    console.log(canvasX, canvasY);

    this.context.beginPath();
    this.context.arc(canvasX, canvasY, 5, 0, Math.PI * 2);
    this.context.fill();
  }
}
